package com.cedricziel.idea.typoscript.lang.lexer;

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

CRLF=\R
WHITE_SPACE=[\ \n\t\f]

SINGLE_QUOTED_STR_CONTENT = ([^\\']|\\([\\'\"/bfnrt]|u[a-fA-F0-9]{4}))+
DOUBLE_QUOTED_STR_CONTENT = ([^\\\"]|\\([\\'\"/bfnrt]|u[a-fA-F0-9]{4}))+

INTEGER_NUMBER = 0|[1-9]\d*
FLOAT_NUMBER = [0-9]*\.[0-9]+([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
IDENTIFIER = [\p{Alpha}\p{Alnum}_:0-9]*

OBJECT_NAME = [A-Z][A-Z_]*

TEXT=[^\n\f\\] | "\\"{CRLF} | "\\".

END_OF_LINE_COMMENT=("#"|"!")[^\r\n]*

%state EXPRESSION
%state SINGLE_QUOTED_STRING
%state DOUBLE_QUOTED_STRING

%%

<YYINITIAL> {
  "."                        { return TypoScriptTypes.DOT; }
  "{"                        { return TypoScriptTypes.LBRACE; }
  "}"                        { return TypoScriptTypes.RBRACE; }
  "="                        { return TypoScriptTypes.ASSIGN; }
  "("                        { return TypoScriptTypes.LPARENTH; }
  ")"                        { return TypoScriptTypes.RPARENTH; }
  "["                        { return TypoScriptTypes.LBRACKET; }
  "]"                        { return TypoScriptTypes.RBRACKET; }

  {END_OF_LINE_COMMENT}      { yybegin(YYINITIAL); return TypoScriptTypes.COMMENT; }
  {OBJECT_NAME}              { return TypoScriptTypes.OBJECT_NAME; }
  {IDENTIFIER}               { return TypoScriptTypes.IDENTIFIER; }
  {TEXT}                     { return TypoScriptTypes.TEXT; }
  ({CRLF}|{WHITE_SPACE})+    { return TokenType.WHITE_SPACE; }

  [^]                        { return TokenType.BAD_CHARACTER; }
}
