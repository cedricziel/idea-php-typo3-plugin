package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.lang.psi.FluidElement;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class ViewHelperReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        /*
         * { f:foo<caret>() }
         */
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(FluidViewHelperReference.class),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    return new PsiReference[]{new ViewHelperReference((FluidElement) element, element.getTextRange())};
                }
            }
        );
    }
}
