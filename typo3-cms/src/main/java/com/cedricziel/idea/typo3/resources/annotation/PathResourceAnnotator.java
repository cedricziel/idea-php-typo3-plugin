package com.cedricziel.idea.typo3.resources.annotation;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLQuotedText;

/**
 * Matches {@link StringLiteralExpression} elements and annotates them if a resource does not exist.
 * <p>
 * Example strings:
 * "EXT:foo/bar/baz.typoscript"
 */
public class PathResourceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof StringLiteralExpression) && !(element instanceof YAMLQuotedText)) {
            return;
        }

        String content = getContents(element);
        if (!content.startsWith("EXT:") || element.getProject().getBasePath() == null) {
            return;
        }

        String resourceName = content.substring(4);
        if (resourceName.contains(":")) {
            // resource name points to a sub-resource such as a translation string, not here.
            return;
        }

        if (ResourcePathIndex.projectContainsResourceFile(element.getProject(), content)) {
            // exact match found
            return;
        }

        if (ResourcePathIndex.projectContainsResourceDirectory(element.getProject(), content)) {
            return;
        }

        createErrorMessage(element, holder, resourceName);
    }

    @NotNull
    private String getContents(@NotNull PsiElement element) {
        if (element instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) element).getContents();
        }

        if (element instanceof YAMLQuotedText) {
            return ((YAMLQuotedText) element).getTextValue();
        }

        return "";
    }

    private void createErrorMessage(@NotNull PsiElement element, @NotNull AnnotationHolder holder, String resourceName) {
        String message = "Resource \"%s\" could not be found in your current project.".replace("%s", resourceName);

        holder.createWarningAnnotation(element, message);
    }
}
