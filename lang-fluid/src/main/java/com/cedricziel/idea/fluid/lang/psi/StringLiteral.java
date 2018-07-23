package com.cedricziel.idea.fluid.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StringLiteral extends PsiElement, PsiLanguageInjectionHost {
    @NotNull
    public String getContents();
}
