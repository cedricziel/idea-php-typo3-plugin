package com.cedricziel.idea.fluid.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArgumentList extends PsiElement {
    @Nullable FluidViewHelperArgument getArgument(@NotNull String argumentName);
}
