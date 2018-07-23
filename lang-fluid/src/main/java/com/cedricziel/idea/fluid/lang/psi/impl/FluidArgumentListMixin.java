package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperArgument;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperArgumentList;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class FluidArgumentListMixin extends FluidElementImpl implements FluidViewHelperArgumentList {
    public FluidArgumentListMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public FluidViewHelperArgument getArgument(@NotNull String argumentName) {
        for (FluidViewHelperArgument fluidViewHelperArgument : getViewHelperArgumentList()) {
            if (fluidViewHelperArgument.getArgumentKey().getText().equals(argumentName)) {
                return fluidViewHelperArgument;
            }
        }

        return null;
    }
}
