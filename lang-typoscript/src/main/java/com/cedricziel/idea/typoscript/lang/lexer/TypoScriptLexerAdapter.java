package com.cedricziel.idea.typoscript.lang.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class TypoScriptLexerAdapter extends FlexAdapter {
  public TypoScriptLexerAdapter() {
      //noinspection RedundantCast
      super(new _TypoScriptLexer((Reader) null));
  }
}
