package com.cedricziel.idea.fluid.lang.lexer;

import com.intellij.lexer.FlexLexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;

%%

%{
  public _FluidLexer() {
    this((java.io.Reader) null);
  }
%}

%class _FluidLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WS = \s

INTEGER_NUMBER = 0|[1-9]\d*
FLOAT_NUMBER = [0-9]*\.[0-9]+([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
IDENTIFIER = [\p{Alpha}_][\p{Alnum}_:]*

%state EXPRESSION
%state SINGLE_QUOTED_STRING
%state DOUBLE_QUOTED_STRING
%state TERNARY_BRANCHES_OP
%state COMMENT

%%

<YYINITIAL> {
  "{"                         { yybegin(EXPRESSION); return FluidTypes.EXPR_START; }
  "<!--/*"                    { yybegin(COMMENT); return FluidTypes.COMMENT_START; }
  "}"                         { yybegin(YYINITIAL); return FluidTypes.EXPR_END; }

  "true"                      { return FluidTypes.BOOLEAN_LITERAL; }
  "false"                     { return FluidTypes.BOOLEAN_LITERAL; }
  "TRUE"                      { return FluidTypes.BOOLEAN_LITERAL; }
  "FALSE"                     { return FluidTypes.BOOLEAN_LITERAL; }

  "'"                         { yybegin(SINGLE_QUOTED_STRING); return FluidTypes.SINGLE_QUOTE; }
  \"                          { yybegin(DOUBLE_QUOTED_STRING); return FluidTypes.DOUBLE_QUOTE; }
  {INTEGER_NUMBER}            { return FluidTypes.INTEGER_NUMBER; }
  {FLOAT_NUMBER}              { return FluidTypes.FLOAT_NUMBER; }
  {IDENTIFIER}                { return FluidTypes.IDENTIFIER; }

  "("                         { return FluidTypes.LEFT_PARENTH; }
  ")"                         { return FluidTypes.RIGHT_PARENTH; }
  "."                         { return FluidTypes.DOT; }
  ","                         { return FluidTypes.COMMA; }
  "!"                         { return FluidTypes.NOT; }
  "&&"                        { return FluidTypes.AND; }
  "||"                        { return FluidTypes.OR; }

  "="                         { return FluidTypes.ASSIGN; }
  "=="                        { return FluidTypes.EQ; }
  "!="                        { return FluidTypes.NEQ; }
  "<"                         { return FluidTypes.LT; }
  ">"                         { return FluidTypes.GT; }
  "<="                        { return FluidTypes.LEQ; }
  ">="                        { return FluidTypes.GEQ; }
  "?"                         { return FluidTypes.TERNARY_QUESTION_OP; }

  {WS}+                       { return TokenType.WHITE_SPACE; }

  [^]                         { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<COMMENT> {
  "*/-->"                     { yybegin(YYINITIAL); return FluidTypes.COMMENT_END; }
  [^]                         { return FluidTypes.COMMENT_CONTENT; }
}
