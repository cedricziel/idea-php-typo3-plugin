package com.cedricziel.idea.fluid.lang.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class FluidLexerAdapter extends FlexAdapter {
    public FluidLexerAdapter() {
        super(new _FluidLexer((Reader) null));
    }
}
