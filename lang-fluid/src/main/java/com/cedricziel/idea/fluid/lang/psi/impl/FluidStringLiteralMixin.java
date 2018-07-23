package com.cedricziel.idea.fluid.lang.psi.impl;

import com.cedricziel.idea.fluid.lang.psi.FluidStringLiteral;
import com.intellij.json.psi.impl.JSStringLiteralEscaper;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public abstract class FluidStringLiteralMixin extends FluidElementImpl implements FluidStringLiteral {
    protected FluidStringLiteralMixin(ASTNode node) {
        super(node);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        ASTNode valueNode = getNode().getFirstChildNode();
        assert valueNode instanceof LeafElement;
        ((LeafElement) valueNode).replaceWithText(text);
        return this;
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new JSStringLiteralEscaper<PsiLanguageInjectionHost>(this) {
            @Override
            protected boolean isRegExpLiteral() {
                return false;
            }
        };
    }

    @NotNull
    public String getContents() {
        String text = getText();
        if (text.startsWith("\"")) {
            return StringUtils.stripStart(StringUtils.stripEnd(text, "\""), "\"");
        }

        return StringUtils.stripStart(StringUtils.stripEnd(text, "'"), "'");
    }
}
