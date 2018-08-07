package com.cedricziel.idea.fluid.variables;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class FluidVariable {
    @NotNull
    final private Set<String> types;

    @Nullable
    private PsiElement psiElement;

    public FluidVariable(@NotNull Set<String> types, @Nullable PsiElement psiElement) {
        this.types = types;
        this.psiElement = psiElement;
    }

    public FluidVariable(@NotNull Set<String> types) {
        this.types = types;
    }

    public FluidVariable(@NotNull String type) {
        this.types = Collections.singleton(type);
    }

    @NotNull
    public Set<String> getTypes() {
        return types;
    }

    @Nullable
    public PsiElement getElement() {
        return psiElement;
    }
}
