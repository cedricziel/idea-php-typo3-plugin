package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperExpr;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

abstract public class FluidViewHelperNodeMixin extends FluidElementImpl implements FluidViewHelperExpr {
    public FluidViewHelperNodeMixin(@NotNull ASTNode node) {
        super(node);
    }

    public @NotNull
    String getPresentableName() {
        String text = this.getBoundNamespace().getText();
        String text1;
        if (this.getViewHelperReference() == null) {
            text1 = "";
        } else {
            text1 = this.getViewHelperReference().getText();
        }

        return text + ":" + text1;
    }
}
