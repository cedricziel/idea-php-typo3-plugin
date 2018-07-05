package com.cedricziel.idea.fluid.lang.psi;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FluidTokenType extends IElementType {
    public FluidTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FluidLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FluidTokenType." + super.toString();
    }
}
