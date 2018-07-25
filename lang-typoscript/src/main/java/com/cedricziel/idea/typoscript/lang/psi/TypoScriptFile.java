package com.cedricziel.idea.typoscript.lang.psi;

import com.cedricziel.idea.typoscript.lang.TypoScriptFileType;
import com.cedricziel.idea.typoscript.lang.TypoScriptLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TypoScriptFile extends PsiFileBase {
  public TypoScriptFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, TypoScriptLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return TypoScriptFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "TypoScript File";
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}
