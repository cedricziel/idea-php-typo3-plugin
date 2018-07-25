package com.cedricziel.idea.typoscript.lang.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class TypoScriptLexerAdapter extends FlexAdapter {
  public TypoScriptLexerAdapter() {
    super(new _TypoScriptLexer((Reader) null));
  }
}
