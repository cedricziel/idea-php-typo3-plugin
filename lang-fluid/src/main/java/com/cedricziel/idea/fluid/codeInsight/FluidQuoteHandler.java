package com.cedricziel.idea.fluid.codeInsight;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class FluidQuoteHandler extends SimpleTokenSetQuoteHandler {
    public FluidQuoteHandler() {
        super(new IElementType[]{FluidTypes.DOUBLE_QUOTED_STRING, FluidTypes.SINGLE_QUOTED_STRING});
    }

    public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
        if (this.isInsideLiteral(iterator)) {
            int start = iterator.getStart();
            return offset == start;
        } else {
            IElementType tokenType = iterator.getTokenType();
            return tokenType == FluidTypes.DOUBLE_QUOTE || tokenType == FluidTypes.SINGLE_QUOTE;
        }
    }

    public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
        if (this.isInsideLiteral(iterator)) {
            int start = iterator.getStart();
            int end = iterator.getEnd();
            return end - start >= 1 && offset == end - 1;
        } else {
            IElementType tokenType = iterator.getTokenType();
            return tokenType == FluidTypes.DOUBLE_QUOTE || tokenType == FluidTypes.SINGLE_QUOTE;
        }
    }

    public boolean hasNonClosedLiteral(@NotNull Editor editor, @NotNull HighlighterIterator iterator, int offset) {
        Document document = editor.getDocument();
        int lineEndOffset = document.getLineEndOffset(document.getLineNumber(offset));
        if (offset < lineEndOffset) {
            CharSequence charSequence = document.getCharsSequence();
            char openQuote = charSequence.charAt(offset);
            int nextCharOffset = offset + 1;
            if (nextCharOffset < lineEndOffset && charSequence.charAt(nextCharOffset) == openQuote) {
                return true;
            }

            for (int i = nextCharOffset + 1; i < lineEndOffset; ++i) {
                if (charSequence.charAt(i) == openQuote) {
                    return false;
                }
            }
        }

        return true;
    }
}
