package com.cedricziel.idea.fluid.codeInsight.template;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class FluidTemplateContext extends TemplateContextType {
    protected FluidTemplateContext() {
        super("FLUID", "Fluid Template");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile psiFile, int i) {
        PsiElement elementAt = psiFile.findElementAt(i);
        if (elementAt != null) {
            return psiFile.getLanguage().is(FluidLanguage.INSTANCE) && !elementAt.getLanguage().is(FluidLanguage.INSTANCE);
        }
        return false;
    }
}
