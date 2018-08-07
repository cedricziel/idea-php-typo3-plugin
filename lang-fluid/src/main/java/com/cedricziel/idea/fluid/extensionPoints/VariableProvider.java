package com.cedricziel.idea.fluid.extensionPoints;

import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface VariableProvider {
    ExtensionPointName<VariableProvider> EP_NAME = ExtensionPointName.create("com.cedricziel.idea.fluid.provider.variables");

    void provide(@NotNull CompletionParameters parameters, ProcessingContext context, Map<String, FluidVariable> variableMap);

    void provide(@NotNull PsiElement element, Map<String, FluidVariable> variableMap);
}
