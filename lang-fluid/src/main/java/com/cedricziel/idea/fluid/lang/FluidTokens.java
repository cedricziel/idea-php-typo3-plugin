package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.psi.tree.TokenSet;

public interface FluidTokens {
    TokenSet tsSTRINGS = TokenSet.create(FluidTypes.STRING_LITERAL);
    TokenSet tsCOMMENTS = TokenSet.create(FluidTypes.TEMPLATE_COMMENT);
}
