package com.cedricziel.idea.typoscript.lang;

import com.intellij.lexer.FlexLexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import com.cedricziel.idea.typoscript.lang.psi.TypoScriptTypes;

%%

%{
  private boolean ternaryBranchesOparatorMatched = false;
  private boolean wsAfterTernaryBranchesOparatorMatched = false;

  public _TypoScriptLexer() {
    this((java.io.Reader) null);
  }
%}

%class _TypoScriptLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WS = \s

SINGLE_QUOTED_STR_CONTENT = ([^\\']|\\([\\'\"/bfnrt]|u[a-fA-F0-9]{4}))+
DOUBLE_QUOTED_STR_CONTENT = ([^\\\"]|\\([\\'\"/bfnrt]|u[a-fA-F0-9]{4}))+

INTEGER_NUMBER = 0|[1-9]\d*
FLOAT_NUMBER = [0-9]*\.[0-9]+([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
IDENTIFIER = [\p{Alpha}_][\p{Alnum}_:]*

%state EXPRESSION
%state SINGLE_QUOTED_STRING
%state DOUBLE_QUOTED_STRING

%%

<YYINITIAL> {
  "{"                        { yybegin(EXPRESSION); return TypoScriptTypes.EXPR_START; }
  "\\{"                      { return TypoScriptTypes.OUTER_TEXT; }
  [^]                        { return TypoScriptTypes.OUTER_TEXT; }
}
