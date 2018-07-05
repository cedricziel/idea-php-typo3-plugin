package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class FluidElementImpl extends ASTWrapperPsiElement implements FluidElement {

  public FluidElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    final String className = getClass().getSimpleName();
    return StringUtil.trimEnd(className, "Impl");
  }
}
