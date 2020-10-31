package com.cedricziel.idea.fluid.lang;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.cedricziel.idea.fluid.file.FluidFileViewProvider.FLUID_FRAGMENT;

public class FluidAstFactory extends ASTFactory {
    @Override
    @Nullable
    public LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        if (type == FLUID_FRAGMENT) {
            return new OuterLanguageElementImpl(type, text);
        }

        return super.createLeaf(type, text);
    }
}
