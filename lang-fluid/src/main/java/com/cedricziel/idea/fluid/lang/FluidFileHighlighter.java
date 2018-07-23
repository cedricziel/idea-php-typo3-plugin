package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.FluidBundle;
import com.cedricziel.idea.fluid.lang.lexer.FluidRawLexerAdapter;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER;

public class FluidFileHighlighter extends SyntaxHighlighterBase {
    public static final Map<TextAttributesKey, Pair<String, HighlightSeverity>> DISPLAY_NAMES = new LinkedHashMap<>();
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(
        "FLUID.BRACES",
        DefaultLanguageHighlighterColors.BRACES
    );

    private static final TextAttributesKey IDENTIFIERS = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.IDENTIFIERS",
        DefaultLanguageHighlighterColors.KEYWORD
    );

    private static final TextAttributesKey COMMENTS = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.COMMENTS",
        DefaultLanguageHighlighterColors.BLOCK_COMMENT
    );

    private static final TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.OPERATORS",
        DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    private static final TextAttributesKey VALUES = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.VALUES",
        DefaultLanguageHighlighterColors.NUMBER
    );

    private static final TextAttributesKey STRINGS = TextAttributesKey.createTextAttributesKey(
        "FLUID.STRINGS",
        DefaultLanguageHighlighterColors.STRING
    );

    private static final TextAttributesKey DATA_PREFIX = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.DATA_PREFIX",
        DefaultLanguageHighlighterColors.KEYWORD
    );

    private static final TextAttributesKey ESCAPE = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.ESCAPE",
        DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    private static final TextAttributesKey OPERATIONS = TextAttributesKey.createTextAttributesKey(
        "FLUID.OPERATION",
        DefaultLanguageHighlighterColors.OPERATION_SIGN
    );

    static {
        keys1 = new HashMap<>();

        keys1.put(FluidTypes.COMMENT_CONTENT, COMMENTS);
        keys1.put(FluidTypes.NUMBER, VALUES);
    }

    static {
        DISPLAY_NAMES.put(IDENTIFIERS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.identifiers.key"), null));
        DISPLAY_NAMES.put(COMMENTS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.comments.key"), null));
        DISPLAY_NAMES.put(OPERATORS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.operators.key"), null));
        DISPLAY_NAMES.put(VALUES, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.values.key"), null));
        DISPLAY_NAMES.put(STRINGS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.strings.key"), null));
        DISPLAY_NAMES.put(DATA_PREFIX, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.data.prefix.key"), null));
        DISPLAY_NAMES.put(ESCAPE, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.escape.key"), null));
    }

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENTS};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIERS};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRINGS};
    private static final TextAttributesKey[] OPERATION_KEYS = new TextAttributesKey[]{OPERATIONS};

    @NotNull
    public Lexer getHighlightingLexer() {
        return new FluidRawLexerAdapter();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(FluidTypes.IDENTIFIER_EXPR) || tokenType.equals(FluidTypes.IDENTIFIER) || tokenType.equals(FluidTypes.NAMESPACE)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(FluidTypes.EXPR_END) || tokenType.equals(FluidTypes.EXPR_START)) {
            return BRACES_KEYS;
        } else if (tokenType.equals(FluidTypes.TEMPLATE_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(FluidTypes.STRING_LITERAL) || tokenType.equals(FluidTypes.DOUBLE_QUOTED_STRING) || tokenType.equals(FluidTypes.SINGLE_QUOTED_STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(FluidTypes.ARROW)) {
            return OPERATION_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
