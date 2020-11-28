package com.cedricziel.idea.fluid.codeInsight.template;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

public class FluidTemplateContext extends TemplateContextType {
    protected FluidTemplateContext() {
        super(FluidLanguage.INSTANCE.getID(), "Fluid Template");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {

        return templateActionContext.getFile().getLanguage() == FluidLanguage.INSTANCE;
    }
}
