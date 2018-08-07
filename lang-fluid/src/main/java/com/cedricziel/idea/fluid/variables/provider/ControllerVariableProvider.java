package com.cedricziel.idea.fluid.variables.provider;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ControllerVariableProvider implements VariableProvider {
    @Override
    public void provide(@NotNull CompletionParameters parameters, ProcessingContext context, Map<String, FluidVariable> variableMap) {
        PsiElement originalPosition = parameters.getOriginalPosition();
        if (originalPosition == null) {
            return;
        }

        PsiFile containingFile = originalPosition.getContainingFile();
        if (!(containingFile instanceof FluidFile)) {
            return;
        }

        variableMap.putAll(FluidUtil.collectControllerVariables((FluidFile) containingFile));
    }

    @Override
    public void provide(@NotNull PsiElement element, Map<String, FluidVariable> variableMap) {
        PsiFile containingFile = element.getContainingFile();
        if (!(containingFile instanceof FluidFile)) {
            return;
        }

        variableMap.putAll(FluidUtil.collectControllerVariables((FluidFile) containingFile));
    }
}
