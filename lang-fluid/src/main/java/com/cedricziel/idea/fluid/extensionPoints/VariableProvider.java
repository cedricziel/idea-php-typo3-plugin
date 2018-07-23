package com.cedricziel.idea.fluid.extensionPoints;

import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface VariableProvider {
    public static ExtensionPointName<VariableProvider> EP_NAME = ExtensionPointName.create("com.cedricziel.idea.fluid.provider.variables");

    public @NotNull Collection<FluidVariable> provide(@NotNull CompletionParameters parameters, ProcessingContext context);

    public @NotNull Collection<FluidVariable> provide(@NotNull PsiElement element);
}
