package com.cedricziel.idea.fluid.codeInsight.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

public class FluidLiveTemplateProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"liveTemplates/Fluid"};
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
