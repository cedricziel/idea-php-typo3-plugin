package com.cedricziel.idea.fluid.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.formatter.common.DefaultInjectedLanguageBlockBuilder;
import com.intellij.psi.formatter.common.InjectedLanguageBlockBuilder;
import com.intellij.psi.formatter.common.InjectedLanguageBlockWrapper;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FluidBlockWithInjection extends FluidBlock {
    private final InjectedLanguageBlockBuilder myInjectedBlockBuilder;

    protected FluidBlockWithInjection(AbstractXmlTemplateFormattingModelBuilder builder, @NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent) {
        super(builder, node, wrap, alignment, settings, xmlFormattingPolicy, indent);

        this.myInjectedBlockBuilder = new FluidBlockWithInjectionBuilder(settings);
    }

    protected List<Block> buildChildren() {
        List<Block> result = new ArrayList<>();
        this.myInjectedBlockBuilder.addInjectedBlocks(result, this.myNode, this.myWrap, this.myAlignment, Indent.getNormalIndent());
        return result;
    }

    @Nullable
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }

    public boolean isLeaf() {
        return false;
    }

    @NotNull
    public List<Block> getSubBlocks() {
        List<Block> result = ContainerUtil.newArrayList();
        this.myInjectedBlockBuilder.addInjectedBlocks(result, this.myNode, this.myWrap, this.myAlignment, Indent.getNoneIndent());

        return result;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    private static class BlockWrapper extends InjectedLanguageBlockWrapper {
        @NotNull
        private final TextRange myRange;
        private final int myOffset;
        private List<Block> myBlocks;

        public BlockWrapper(@NotNull Block original, int offset, @NotNull TextRange range, @Nullable Indent indent, @Nullable Language language) {
            super(original, offset, range, indent, language);

            this.myOffset = offset;
            this.myRange = range;
        }

        @NotNull
        public List<Block> getSubBlocks() {
            if (this.myBlocks == null) {
                this.myBlocks = this.buildBlocks();
            }

            return this.myBlocks;
        }

        @NotNull
        private List<Block> buildBlocks() {
            List<Block> list = this.getOriginal().getSubBlocks();
            if (list.isEmpty()) {

                return AbstractBlock.EMPTY;
            } else {
                ArrayList<Block> result = new ArrayList<>(list.size());
                Iterator var3 = list.iterator();

                while(var3.hasNext()) {
                    Block block = (Block)var3.next();
                    if (block.getTextRange().intersects(this.myRange)) {
                        result.add(new InjectedLanguageBlockWrapper(block, this.myOffset, this.myRange, null, this.getLanguage()));
                    }
                }

                return result;
            }
        }
    }

    private static class FluidBlockWithInjectionBuilder extends DefaultInjectedLanguageBlockBuilder {
        public FluidBlockWithInjectionBuilder(@NotNull CodeStyleSettings settings) {
            super(settings);
        }

        @NotNull
        public Block createInjectedBlock(@NotNull ASTNode node, @NotNull Block originalBlock, Indent indent, int offset, TextRange range, @NotNull Language language) {

            return new BlockWrapper(originalBlock, offset, range, indent, language);
        }
    }
}
