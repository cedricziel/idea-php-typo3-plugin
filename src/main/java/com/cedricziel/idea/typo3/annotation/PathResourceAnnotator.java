package com.cedricziel.idea.typo3.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Matches {@link StringLiteralExpression} elements and annotates them if a resource does not exist.
 * <p>
 * Example strings:
 * "EXT:foo/bar/baz.typoscript"
 */
public class PathResourceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof StringLiteralExpression)) {
            return;
        }

        String content = ((StringLiteralExpression) element).getContents();
        if (!content.startsWith("EXT:") || element.getProject().getBasePath() == null) {
            return;
        }

        String resourceName = content.substring(4, content.length());
        if (resourceName.contains(":")) {
            // resource name points to a sub-resource such as a translation string, not here.
            return;
        }

        String[] split = resourceName.split("/");
        String fileName = split[split.length - 1];
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir(element.getProject().getBasePath());
        scanner.addDefaultExcludes();
        scanner.setIncludes(new String[]{"**/" + resourceName + "/"});
        scanner.scan();

        String[] paths = scanner.getIncludedFiles();
        if (paths.length != 0) {
            // the resource is a directory
            return;
        }

        List<PsiFile> filesByName = Arrays.asList(FilenameIndex.getFilesByName(element.getProject(), fileName, GlobalSearchScope.allScope(element.getProject())));
        if (filesByName.size() == 0) {
            createErrorMessage(element, holder, resourceName);

            return;
        }

        for (PsiFile file : filesByName) {
            VirtualFile virtualFile = file.getVirtualFile();


            if (virtualFile.getPath().contains("typo3conf/ext/" + resourceName) || virtualFile.getPath().contains("sysext/" + resourceName)) {
                // all good
                return;
            }

            if (virtualFile.isDirectory() && virtualFile.getPath().endsWith(resourceName)) {
                return;
            }
        }

        createErrorMessage(element, holder, resourceName);
    }

    private void createErrorMessage(@NotNull PsiElement element, @NotNull AnnotationHolder holder, String resourceName) {
        String message = "Resource \"%s\" could not be found in your current project.".replace("%s", resourceName);
        holder.createErrorAnnotation(element, message);
    }
}
