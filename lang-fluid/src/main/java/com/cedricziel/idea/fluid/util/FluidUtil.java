package com.cedricziel.idea.fluid.util;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.FluidElement;
import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
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

    public static FluidElement retrieveFluidElementAtPosition(PsiElement psiElement) {
        FileViewProvider viewProvider = psiElement.getContainingFile().getViewProvider();
        if (!viewProvider.getLanguages().contains(FluidLanguage.INSTANCE)) {
            return null;
        }

        int textOffset = psiElement.getTextOffset();
        FluidFile psi = (FluidFile) viewProvider.getPsi(FluidLanguage.INSTANCE);

        PsiElement elementAt = psi.findElementAt(textOffset);
        if (elementAt == null) {
            return null;
        }

        return (FluidElement) elementAt;
    }

    public static FluidElement retrieveFluidElementAtPosition(PsiFile psiFile, int startOffset) {
        FileViewProvider viewProvider = psiFile.getViewProvider();
        if (!viewProvider.getLanguages().contains(FluidLanguage.INSTANCE)) {
            return null;
        }

        FluidFile psi = (FluidFile) viewProvider.getPsi(FluidLanguage.INSTANCE);

        PsiElement elementAt = psi.findElementAt(startOffset);
        if (elementAt == null) {
            return null;
        }

        return (FluidElement) elementAt;
    }
}
