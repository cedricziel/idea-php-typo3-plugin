package com.cedricziel.idea.fluid.formatter;

import com.cedricziel.idea.fluid.file.FluidFileViewProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.lang.psi.FluidStatement;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.IElementType;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidFormattingModelBuilder extends AbstractXmlTemplateFormattingModelBuilder {
    @Override
    protected boolean isTemplateFile(PsiFile file) {

        return file instanceof FluidFile;
    }

    @Override
    public boolean isOuterLanguageElement(PsiElement element) {
        return element.getNode().getElementType() == FluidFileViewProvider.FLUID_FRAGMENT;
    }

    @Override
    public boolean isMarkupLanguageElement(PsiElement element) {
        IElementType theType = element.getNode().getElementType();

        return theType == FluidFileViewProvider.TEMPLATE_DATA;
    }

    @Override
    protected Block createTemplateLanguageBlock(ASTNode node, CodeStyleSettings settings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, @Nullable Alignment alignment, @Nullable Wrap wrap) {
        PsiElement nodePsi = node.getPsi();

        return (nodePsi instanceof FluidStatement && hasInjection(nodePsi) ? new FluidBlockWithInjection(this, node, wrap, alignment, settings, xmlFormattingPolicy, indent) : new FluidBlock(this, node, wrap, alignment, settings, xmlFormattingPolicy, indent));
    }

    public static boolean hasInjection(PsiElement host) {
        List<Pair<PsiElement, TextRange>> psiFiles = InjectedLanguageManager.getInstance(host.getProject()).getInjectedPsiFiles(host);

        return psiFiles != null && psiFiles.size() > 0;
    }
}
