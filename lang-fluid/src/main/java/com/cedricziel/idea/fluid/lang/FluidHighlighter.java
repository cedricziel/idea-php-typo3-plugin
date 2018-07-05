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
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FluidHighlighter extends SyntaxHighlighterBase {
    public static final Map<TextAttributesKey, Pair<String, HighlightSeverity>> DISPLAY_NAMES = new LinkedHashMap<>();
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final Map<IElementType, TextAttributesKey> keys2;
    private static final TextAttributesKey MUSTACHES = TextAttributesKey.createTextAttributesKey(
        "HANDLEBARS.MUSTACHES",
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
        "HANDLEBARS.STRINGS",
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

    static {
        keys1 = new HashMap<>();
        keys2 = new HashMap<>();

        keys1.put(FluidTypes.COMMENT_CONTENT, COMMENTS);
        keys1.put(FluidTypes.NUMBER, VALUES);
    }

    static {
        DISPLAY_NAMES.put(MUSTACHES, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.mustaches.key"), null));
        DISPLAY_NAMES.put(IDENTIFIERS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.identifiers.key"), null));
        DISPLAY_NAMES.put(COMMENTS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.comments.key"), null));
        DISPLAY_NAMES.put(OPERATORS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.operators.key"), null));
        DISPLAY_NAMES.put(VALUES, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.values.key"), null));
        DISPLAY_NAMES.put(STRINGS, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.strings.key"), null));
        DISPLAY_NAMES.put(DATA_PREFIX, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.data.prefix.key"), null));
        DISPLAY_NAMES.put(ESCAPE, new Pair<>(FluidBundle.message("fl.page.colors.descriptor.escape.key"), null));
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new FluidRawLexerAdapter();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType), keys2.get(tokenType));
    }
}
