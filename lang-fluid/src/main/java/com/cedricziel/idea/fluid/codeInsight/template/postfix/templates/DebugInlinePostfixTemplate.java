package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.cedricziel.idea.fluid.FluidPatterns;
import com.cedricziel.idea.fluid.codeInsight.template.LiveTemplateFactory;
import com.cedricziel.idea.fluid.lang.psi.FluidInlineStatement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class DebugInlinePostfixTemplate extends PostfixTemplate {
    protected DebugInlinePostfixTemplate(FluidPostfixTemplateProvider fluidPostfixTemplateProvider) {
        super("f:debug", "deb", "Pipes the expression result into a debug statement", fluidPostfixTemplateProvider);
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        return FluidPatterns
            .inlinePostfixPositionPattern()
            .accepts(context);
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        FluidInlineStatement expression = (FluidInlineStatement) PsiTreeUtil.findFirstParent(context, psiElement -> psiElement instanceof FluidInlineStatement);
        if (expression == null) {
            return;
        }

        Template tagTemplate = LiveTemplateFactory.createInlinePipeToDebugTemplate(expression);
        tagTemplate.addVariable("EXPR", new TextExpression(expression.getInlineChain().getText()), true);

        int textOffset = expression.getTextOffset() + expression.getTextLength();
        editor.getCaretModel().moveToOffset(textOffset);

        TemplateManager.getInstance(context.getProject()).startTemplate(editor, tagTemplate);
        PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());

        expression.delete();
    }
}
