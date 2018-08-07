package com.cedricziel.idea.fluid.variables.provider;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AllVariableProvider implements VariableProvider {

    private static final FluidVariable ALL;

    static {
        ALL = new FluidVariable("_all");
    }

    @Override
    public void provide(@NotNull CompletionParameters parameters, ProcessingContext context, Map<String, FluidVariable> variableMap) {
        doProvide(variableMap);
    }

    private synchronized void doProvide(Map<String, FluidVariable> variableMap) {
        variableMap.put("_all", ALL);
    }

    @Override
    public void provide(@NotNull PsiElement element, Map<String, FluidVariable> variableMap) {
        doProvide(variableMap);
    }
}
