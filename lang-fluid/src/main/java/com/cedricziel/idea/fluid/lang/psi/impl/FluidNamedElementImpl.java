package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class FluidNamedElementImpl extends ASTWrapperPsiElement implements FluidNamedElement {
  public FluidNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }
}
