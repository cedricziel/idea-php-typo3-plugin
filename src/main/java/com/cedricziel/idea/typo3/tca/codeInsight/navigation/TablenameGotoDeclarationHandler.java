package com.cedricziel.idea.typo3.tca.codeInsight.navigation;

import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import static com.cedricziel.idea.typo3.util.TableUtil.getExtTablesSqlFilesForTable;

public class TablenameGotoDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (!isTablenameTcaField(sourceElement)) {
            return new PsiElement[0];
        }

        return getExtTablesSqlFilesForTable(sourceElement.getText(), sourceElement.getProject());
    }

    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }

    private boolean isTablenameTcaField(PsiElement psiElement) {

        return TCAUtil.arrayIndexIsTCATableNameField(psiElement);
    }
}
