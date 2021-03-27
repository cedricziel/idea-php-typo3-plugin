package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.lang.FluidReference;
import com.cedricziel.idea.fluid.lang.psi.FluidElement;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperExpr;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import org.jetbrains.annotations.NotNull;

public class ViewHelperReference extends FluidReference {

    public ViewHelperReference(@NotNull FluidElement psiElement) {
        super(psiElement);
    }

    public ViewHelperReference(FluidElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myElement instanceof FluidViewHelperReference) {
            FluidViewHelperExpr viewHelperExpr = (FluidViewHelperExpr) PsiTreeUtil.findFirstParent(myElement, e -> e instanceof FluidViewHelperExpr);
            String presentableName = viewHelperExpr.getPresentableName();
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
    public Object @NotNull [] getVariants() {
        return new Object[0];
    }
}
