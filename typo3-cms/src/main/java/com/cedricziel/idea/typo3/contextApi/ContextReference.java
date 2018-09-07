package com.cedricziel.idea.typo3.contextApi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

public class ContextReference extends PsiPolyVariantReferenceBase<PsiElement> {
    public ContextReference(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
