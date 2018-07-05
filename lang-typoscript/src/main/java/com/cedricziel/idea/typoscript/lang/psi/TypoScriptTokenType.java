package com.cedricziel.idea.typoscript.lang.psi;

import com.cedricziel.idea.typoscript.lang.TypoScriptLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TypoScriptTokenType extends IElementType {
    public TypoScriptTokenType(@NotNull @NonNls String debugName) {
        super(debugName, TypoScriptLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "TypoScriptTokenType." + super.toString();
    }
}
