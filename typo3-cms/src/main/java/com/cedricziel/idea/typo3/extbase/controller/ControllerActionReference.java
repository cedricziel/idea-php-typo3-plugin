package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.util.ControllerActionUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerActionReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String actionName;
    private final String fullActionName;

    public ControllerActionReference(StringLiteralExpression psiElement) {
        super(psiElement);

        this.actionName = psiElement.getContents();
        this.fullActionName = (psiElement.getContents() + "Action");
    }

    public ControllerActionReference(StringLiteralExpression psiElement, TextRange textRange) {
        super(psiElement, textRange);

        final String actionName = psiElement.getContents().substring(textRange.getStartOffset() - 1, textRange.getEndOffset() - 1);

        this.actionName = actionName;
        this.fullActionName = actionName + "Action";
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(ControllerActionUtil.getDefinitionElements(myElement.getProject(), actionName));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return ControllerActionUtil.createLookupElements(myElement.getProject());
    }
}
