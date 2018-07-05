package com.cedricziel.idea.fluid.lang.lexer;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.lexer.MergeFunction;
import com.intellij.lexer.MergingLexerAdapterBase;

public class FluidLexer extends MergingLexerAdapterBase {
    public FluidLexer() {
        super(new FluidRawLexerAdapter());
    }

    @Override
    public MergeFunction getMergeFunction() {
        return (type, originalLexer) -> {
            if (FluidTypes.COMMENT_START != type) {
                return type;
            }

            if (originalLexer.getTokenType() == FluidTypes.COMMENT_CONTENT) {
                originalLexer.advance();
            }

            if (originalLexer.getTokenType() == FluidTypes.COMMENT_END) {
                originalLexer.advance();
                return FluidTypes.COMMENT_END;
            }

            return type;
        };
    }
}
