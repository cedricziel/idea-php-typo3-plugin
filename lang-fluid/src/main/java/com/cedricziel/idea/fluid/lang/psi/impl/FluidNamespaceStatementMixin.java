package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidAccessibleNamespaceStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidNamespaceStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperNamespace;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

abstract public class FluidNamespaceStatementMixin extends FluidElementImpl implements FluidNamespaceStatement, FluidAccessibleNamespaceStatement {
    public FluidNamespaceStatementMixin(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public String getAlias() {
        for (PsiElement child : getChildren()) {
            child.getText();
        }
        PsiElement childByType = this.findChildByType(FluidTypes.NAMESPACE_ALIAS);
        if (childByType == null) {
            return "";
        }

        return childByType.getText();
    }

    @NotNull
    public String getNamespace() {
        FluidViewHelperNamespace viewHelperNamespace = getViewHelperNamespace();
        if (viewHelperNamespace == null) {
            return "";
        }

        return this.getViewHelperNamespace().getText().replace("\\", "/");
    }
}
