package com.cedricziel.idea.fluid.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import com.intellij.xml.template.formatter.TemplateLanguageBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidBlock extends TemplateLanguageBlock {
    protected FluidBlock(AbstractXmlTemplateFormattingModelBuilder builder, @NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent) {
        super(builder, node, wrap, alignment, settings, xmlFormattingPolicy, indent);
    }

    @NotNull
    @Override
    protected Indent getChildIndent(@NotNull ASTNode node) {
        return null;
    }

    @Override
    protected Spacing getSpacing(TemplateLanguageBlock adjacentBlock) {
        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }
}
