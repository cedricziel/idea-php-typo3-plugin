package com.cedricziel.idea.typoscript.lang.psi;

import com.cedricziel.idea.typoscript.lang.TypoScriptLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TypoScriptElementType extends IElementType {
  public TypoScriptElementType(@NotNull @NonNls String debugName) {
    super(debugName, TypoScriptLanguage.INSTANCE);
  }
}
