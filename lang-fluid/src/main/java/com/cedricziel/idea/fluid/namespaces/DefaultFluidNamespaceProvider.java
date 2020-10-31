package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class DefaultFluidNamespaceProvider implements NamespaceProvider {
    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        return Collections.singletonList(new FluidNamespace("f", "TYPO3/Fluid/ViewHelpers"));
    }
}
