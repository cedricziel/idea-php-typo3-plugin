package com.cedricziel.idea.typo3.codeInsight.navigation;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.cedricziel.idea.typo3.util.ResourceUtil.isExtResourcePath;

public class PathResourceGotoDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null || !TYPO3CMSProjectSettings.isEnabled(sourceElement)) {
            return PsiElement.EMPTY_ARRAY;
        }

        if (!isExtResourcePath(sourceElement)) {
            return emptyPsiElementArray();
        }

        String identifier = sourceElement.getText();
        if (ResourcePathIndex.projectContainsResourceDirectory(sourceElement.getProject(), identifier)) {
            return emptyPsiElementArray();
        }

        return ResourcePathIndex.findElementsForKey(sourceElement.getProject(), identifier);

    }

    @NotNull
    private PsiElement[] emptyPsiElementArray() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public String getActionText(@NotNull DataContext context) {
        return null;
    }
}
