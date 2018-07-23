package com.cedricziel.idea.fluid.variables;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AllVariableProvider implements VariableProvider {

    private static final FluidVariable ALL;

    static {
        ALL = new FluidVariable("_all").setDescription("Pseudo variable containing every available variable in the context");
    }

    @NotNull
    @Override
    public Collection<FluidVariable> provide(@NotNull CompletionParameters parameters, ProcessingContext context) {
        return ContainerUtil.list(ALL);
    }

    @NotNull
    @Override
    public Collection<FluidVariable> provide(@NotNull PsiElement element) {
        return ContainerUtil.list(ALL);
    }
}
