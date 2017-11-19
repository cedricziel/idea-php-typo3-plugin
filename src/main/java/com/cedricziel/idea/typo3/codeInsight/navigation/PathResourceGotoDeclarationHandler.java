package com.cedricziel.idea.typo3.codeInsight.navigation;

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
        if (!isExtResourcePath(sourceElement)) {
            return emptyPsiElementArray();
        }

        if (sourceElement != null) {
            String identifier = sourceElement.getText();
            return ResourcePathIndex.findElementsForKey(sourceElement.getProject(), identifier);
        }

        return emptyPsiElementArray();
    }

    @NotNull
    private PsiElement[] emptyPsiElementArray() {
        return new PsiElement[0];
    }

    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }
}
