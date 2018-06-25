package com.cedricziel.idea.fluid.lang;

import com.intellij.lang.Language;
import com.intellij.psi.templateLanguages.TemplateLanguage;

public class FluidLanguage extends Language implements TemplateLanguage {
    public static final FluidLanguage INSTANCE = new FluidLanguage();

    private FluidLanguage() {
        super("Fluid");
    }
}
