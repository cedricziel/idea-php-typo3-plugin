package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperExpr;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import org.jetbrains.annotations.NotNull;

public class ViewHelperReference extends PsiPolyVariantReferenceBase<PsiElement> {
    public ViewHelperReference(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (myElement instanceof FluidViewHelperExpr) {
            String presentableName = ((FluidViewHelperExpr) myElement).getPresentableName();
            ViewHelper viewHelperByName = ViewHelperUtil.getViewHelperByName(myElement, presentableName);
            if (viewHelperByName == null) {

                return ResolveResult.EMPTY_ARRAY;
            }

            return PsiElementResolveResult.createResults(PhpIndex.getInstance(myElement.getProject()).getClassesByFQN(viewHelperByName.fqn));
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
