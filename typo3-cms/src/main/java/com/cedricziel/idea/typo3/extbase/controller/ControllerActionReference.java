package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.util.ControllerActionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerActionReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String actionName;

    public ControllerActionReference(StringLiteralExpression psiElement) {
        super(psiElement);

        this.actionName = psiElement.getContents();
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
