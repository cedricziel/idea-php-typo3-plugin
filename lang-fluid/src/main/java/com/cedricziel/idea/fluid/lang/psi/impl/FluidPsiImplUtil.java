package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidBoundNamespace;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperArgument;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperArgumentList;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidPsiImplUtil {
    public FluidBoundNamespace getAlias() {
        return null;
    }

    @Nullable
    public FluidViewHelperArgument getArgument(@NotNull FluidViewHelperArgumentList argumentList, @NotNull String argumentName) {
        for (FluidViewHelperArgument argument : argumentList.getViewHelperArgumentList()) {
            if (argument.getArgumentKey().getText().equals(argumentName)) {
                return argument;
            }
        }

        return null;
    }

    public boolean hasArgument(@NotNull FluidViewHelperArgumentList argumentList, @NotNull String argumentName) {
        return getArgument(argumentList, argumentName) != null;
    }

    public static String getName(FluidViewHelperReference element) {
        return element.getText();
    }

    public static PsiElement setName(FluidViewHelperReference element, String newName) {
        return element;
    }
}
