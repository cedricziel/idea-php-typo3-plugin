package com.cedricziel.idea.fluid.lang.psi;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FluidElementType extends IElementType {
    public FluidElementType(@NotNull @NonNls String debugName) {
        super(debugName, FluidLanguage.INSTANCE);
    }
}
