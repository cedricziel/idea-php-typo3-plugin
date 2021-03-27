package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidBraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair @NotNull [] getPairs() {
        return new BracePair[]{
            new BracePair(FluidTypes.LEFT_PARENTH, FluidTypes.RIGHT_PARENTH, false),
            new BracePair(FluidTypes.EXPR_START, FluidTypes.EXPR_END, true),
        };
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return false;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return 0;
    }
}
