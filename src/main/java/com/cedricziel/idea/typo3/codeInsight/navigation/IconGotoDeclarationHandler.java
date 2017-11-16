package com.cedricziel.idea.typo3.codeInsight.navigation;

import com.cedricziel.idea.typo3.index.IconIndex;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

public class IconGotoDeclarationHandler implements GotoDeclarationHandler {
    /**
     * Provides an array of target declarations for given {@code sourceElement}.
     *
     * @param sourceElement input PSI element
     * @param offset        offset in the file
     * @param editor
     * @return all target declarations as an array of {@code PsiElement} or null if none were found
     */
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement != null && PlatformPatterns.psiElement().withParent(StringLiteralExpression.class).accepts(sourceElement)) {
            String iconIdentifier = ((StringLiteralExpression) sourceElement.getParent()).getContents();
            if (IconIndex.getAllAvailableIcons(sourceElement.getProject()).contains(iconIdentifier)) {
                return IconIndex.getIconDefinitionElements(sourceElement.getProject(), iconIdentifier);
            }
        }

        return new PsiElement[0];
    }

    /**
     * Provides the custom action text.
     *
     * @param context the action data context
     * @return the custom text or null to use the default text
     */
    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }
}
