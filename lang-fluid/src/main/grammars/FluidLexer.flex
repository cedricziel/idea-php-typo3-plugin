package com.cedricziel.idea.fluid.lang.lexer;

import com.intellij.lexer.FlexLexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidTokenType;

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

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WS=({LINE_WS}|{EOL})+

ALL_VARIABLE = '_all'

INTEGER_NUMBER = 0|[1-9]\d*
FLOAT_NUMBER = [0-9]*\.[0-9]+([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
IDENTIFIER = [\p{Alpha}_][\p{Alnum}_]*
VIEWHELPER_CALL = [\p{Alpha}_][\p{Alnum}_:]*
VIEWHELPER_NAME= [a-zA-Z]*?(.[a-zA-Z])*

SINGLE_QUOTED_STRING=((\" ([^\"\n])* \"?) | ("'" ([^\'\n])* \'?))
DOUBLE_QUOTED_STRING=((\" ([^\"\n])* \"?) | ("\"" ([^\"\n])* \"?))

NAMESPACE_DECL = [a-zA-Z\\\]+][\\\a-zA-Z]+]*
NAMESPACE_ALIAS= [a-z]+

TEXT = [^{]*

%state EXPRESSION_LIST
%state NAMESPACE_IMPORT
%state VIEW_HELPER
%state SECTION_NODE
%state RENDER_NODE
%state LAYOUT_NODE
%state ARGUMENT_LIST
%state SINGLE_QUOTED_STRING
%state DOUBLE_QUOTED_STRING
%state TERNARY_BRANCHES_OP
%state COMMENT

%%

<YYINITIAL> {
  {TEXT}                      { return FluidTypes.TEXT; }
  "{"                         { yybegin(EXPRESSION_LIST); return FluidTypes.EXPR_START; }
  "<!--/*"                    { yybegin(COMMENT); return FluidTypes.COMMENT_START; }
  "}"                         { yybegin(YYINITIAL); return FluidTypes.EXPR_END; }

  [^]                         { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<LAYOUT_NODE> {
  {IDENTIFIER}                { return FluidTypes.IDENTIFIER; }
  "="                         { return FluidTypes.ASSIGN; }
  {DOUBLE_QUOTED_STRING}      { return FluidTypes.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING}      { return FluidTypes.SINGLE_QUOTED_STRING; }
  {WS}+                       { return TokenType.WHITE_SPACE; }

  "/>"                        { yybegin(YYINITIAL); return FluidTypes.TAG_END; }

  [^]                         { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<EXPRESSION_LIST> {
  "}"                         { yybegin(YYINITIAL); return FluidTypes.EXPR_END; }
  "true"                      { return FluidTypes.TRUE; }
  "false"                     { return FluidTypes.FALSE; }
  "TRUE"                      { return FluidTypes.TRUE; }
  "FALSE"                     { return FluidTypes.FALSE; }

  '_all'                      { return FluidTypes.VAR_ALL; }
  'as'                        { return FluidTypes.AS; }

  "->"                        { return FluidTypes.ARROW; }
  ":"                         { return FluidTypes.COLON; }
  "'"                         { return FluidTypes.SINGLE_QUOTE; }
  "\""                        { return FluidTypes.DOUBLE_QUOTE; }

  {DOUBLE_QUOTED_STRING}      { return FluidTypes.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING}      { return FluidTypes.SINGLE_QUOTED_STRING; }
  "namespace"                 { yybegin(NAMESPACE_IMPORT); return FluidTypes.NAMESPACE; }
  {INTEGER_NUMBER}            { return FluidTypes.NUMBER; }
  {FLOAT_NUMBER}              { return FluidTypes.FLOAT_NUMBER; }

  "("                         { return FluidTypes.LEFT_PARENTH; }
  ")"                         { return FluidTypes.RIGHT_PARENTH; }
  "."                         { return FluidTypes.DOT; }
  ","                         { return FluidTypes.COMMA; }
  "!"                         { return FluidTypes.NOT; }
  "&&"                        { return FluidTypes.AND; }
  "||"                        { return FluidTypes.OR; }

  "+"                         { return FluidTypes.PLUS;}
  "-"                         { return FluidTypes.MINUS;}

  "/"                         { return FluidTypes.DIV;}
  "*"                         { return FluidTypes.MUL;}
  "%"                         { return FluidTypes.MOD;}

  "="                         { return FluidTypes.ASSIGN; }
  "=="                        { return FluidTypes.EQ; }
  "!="                        { return FluidTypes.NEQ; }
  "<"                         { return FluidTypes.LT; }
  ">"                         { return FluidTypes.GT; }
  "<="                        { return FluidTypes.LEQ; }
  ">="                        { return FluidTypes.GEQ; }
  "?"                         { return FluidTypes.TERNARY; }
  "->"                        { return FluidTypes.ARROW; }
  ":"                         { return FluidTypes.COLON; }

  {IDENTIFIER}                { return FluidTypes.IDENTIFIER; }

  {WS}+                       { return TokenType.WHITE_SPACE; }

  [^]                         { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<NAMESPACE_IMPORT> {
  "="                         { return FluidTypes.ASSIGN; }
  {WS}+                       { return TokenType.WHITE_SPACE; }
  {NAMESPACE_DECL}            { return FluidTypes.NS; }
  {IDENTIFIER}                { return FluidTypes.NAMESPACE_ALIAS; }
  "}"                         { yybegin(YYINITIAL); return FluidTypes.EXPR_END; }

  [^]                         { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<COMMENT> {
  "*/-->"                     { yybegin(YYINITIAL); return FluidTypes.COMMENT_END; }

  [^]                         { return FluidTypes.COMMENT_CONTENT; }
}
