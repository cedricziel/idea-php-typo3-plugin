package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.lang.psi.FluidElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import org.jetbrains.annotations.NotNull;

abstract public class FluidReference extends PsiPolyVariantReferenceBase<FluidElement> {
    public FluidReference(@NotNull FluidElement psiElement) {
        super(psiElement);
    }

    public FluidReference(FluidElement element, TextRange textRange) {
        super(element, textRange);
    }
}
