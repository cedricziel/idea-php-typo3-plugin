package com.cedricziel.idea.fluid.util;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.psi.PsiElement;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FluidUtil {
    public static Map<String, FluidVariable> findVariablesInCurrentContext(@NotNull PsiElement element) {
        Map<String, FluidVariable> variables = new THashMap<>();
        for (VariableProvider extension : VariableProvider.EP_NAME.getExtensions()) {
            extension.provide(element).forEach(v -> variables.put(v.identifier, v));
        }

        return variables;
    }
}
