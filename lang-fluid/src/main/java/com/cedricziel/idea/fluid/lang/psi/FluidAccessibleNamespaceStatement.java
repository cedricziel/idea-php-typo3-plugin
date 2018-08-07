package com.cedricziel.idea.fluid.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface FluidAccessibleNamespaceStatement extends PsiElement {
    @NotNull
    String getNamespace();

    @NotNull
    String getAlias();
}
