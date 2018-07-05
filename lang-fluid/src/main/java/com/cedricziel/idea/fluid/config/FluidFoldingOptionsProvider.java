package com.cedricziel.idea.fluid.config;

import com.cedricziel.idea.fluid.FluidBundle;
import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;

public class FluidFoldingOptionsProvider
    extends BeanConfigurable<FluidFoldingOptionsProvider.FluidCodeFoldingOptionsBean> implements CodeFoldingOptionsProvider {

    public FluidFoldingOptionsProvider() {
        super(new FluidCodeFoldingOptionsBean());

        FluidCodeFoldingOptionsBean settings = getInstance();
        checkBox(FluidBundle.message("fl.pages.folding.auto.collapse.blocks"), settings::isAutoCollapseBlocks, settings::setAutoCollapseBlocks);
    }

    public static class FluidCodeFoldingOptionsBean {
        public boolean isAutoCollapseBlocks() {
            return FluidConfig.isAutoCollapseBlocksEnabled();
        }

        public void setAutoCollapseBlocks(boolean value) {
            FluidConfig.setAutoCollapseBlocks(value);
        }
    }
}
