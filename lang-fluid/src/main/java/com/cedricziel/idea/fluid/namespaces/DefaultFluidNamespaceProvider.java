package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DefaultFluidNamespaceProvider implements NamespaceProvider {
    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        return ContainerUtil.list(new FluidNamespace("f", "TYPO3/Fluid/ViewHelpers"));
    }
}
