package com.cedricziel.idea.fluid.lang.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class FluidRawLexerAdapter extends FlexAdapter {
    public FluidRawLexerAdapter() {
        super(new _FluidLexer((Reader) null));
    }
}
