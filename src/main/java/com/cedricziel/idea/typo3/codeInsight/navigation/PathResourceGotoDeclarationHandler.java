package com.cedricziel.idea.typo3.codeInsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.cedricziel.idea.typo3.util.ResourceUtil.isExtResourcePath;

public class PathResourceGotoDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (!isExtResourcePath(sourceElement)) {
            return new PsiElement[0];
        }

        if (sourceElement != null) {
            String text = sourceElement.getText();
            String fileName = text.split("/")[text.split("/").length - 1];
            String relativePath = text.replaceFirst("EXT:", "");

            List<PsiFile> psiFiles = Arrays.asList(
                    FilenameIndex.getFilesByName(
                            sourceElement.getProject(),
                            fileName,
                            GlobalSearchScope.allScope(sourceElement.getProject())
                    ));

            return psiFiles
                    .stream()
                    .filter(x -> x.getVirtualFile().getPath().contains(relativePath))
                    .toArray(PsiElement[]::new);
        }

        return new PsiElement[0];
    }

    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }
}
