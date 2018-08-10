package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.cedricziel.idea.typo3.resources.ResourceLookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResourceUtil {
    public static boolean isExtResourcePath(PsiElement element) {
        // must be a string element
        if (!PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(StringLiteralExpression.class)).accepts(element)) {
            return false;
        }

        String currentText = element.getParent().getText();

        return element.getProject().getBasePath() != null && currentText.contains("EXT:");
    }

    public static PsiElement[] getResourceDefinitionElements(@NotNull Project project, @NotNull String resourceName) {
        if (!ResourcePathIndex.projectContainsResourceFile(project, resourceName)) {
            return null;
        }

        return ResourcePathIndex.findElementsForKey(project, resourceName);
    }

    public static Object[] getResourceLookupElements(@NotNull Project project) {

        List<ResourceLookupElement> result = new ArrayList<>();
        ResourcePathIndex.getAvailableExtensionResourceFiles(project).forEach(identifier -> {
            result.add(new ResourceLookupElement(identifier));
        });

        return result.toArray();
    }
}
