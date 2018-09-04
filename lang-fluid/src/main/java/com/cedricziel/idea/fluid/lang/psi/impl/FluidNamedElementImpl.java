package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidElementFactory;
import com.cedricziel.idea.fluid.lang.psi.FluidNamedElement;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class FluidNamedElementImpl extends FluidElementImpl implements FluidNamedElement {
    public FluidNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        return this.getNode().findChildByType(FluidTypes.IDENTIFIER).getText();
    }

    public PsiElement setName(@NotNull String newName) {
        ASTNode keyNode = this.getNode().findChildByType(FluidTypes.IDENTIFIER);
        if (keyNode != null) {
            FluidViewHelperReference viewHelperReference = FluidElementFactory.createViewHelperReference(this.getProject(), newName);
            ASTNode newKeyNode = viewHelperReference.getFirstChild().getNode();
            this.getNode().replaceChild(keyNode, newKeyNode);
        }

        return this;
    }
}
