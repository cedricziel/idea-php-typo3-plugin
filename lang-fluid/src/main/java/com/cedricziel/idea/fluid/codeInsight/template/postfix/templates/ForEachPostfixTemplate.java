package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.cedricziel.idea.fluid.codeInsight.template.LiveTemplateFactory;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class ForEachPostfixTemplate extends PostfixTemplate {
    protected ForEachPostfixTemplate(FluidPostfixTemplateProvider fluidPostfixTemplateProvider) {
        super("f:for", "for", "Wrap element in f:for loop", fluidPostfixTemplateProvider);
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        return PlatformPatterns.or(
                PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(
                    PlatformPatterns.psiElement(FluidFieldChain.class)
                ),
                PlatformPatterns.and(
                    PlatformPatterns.psiElement().withParent(
                        FluidViewHelperExpr.class
                    ),
                    PlatformPatterns.psiElement(FluidTypes.RIGHT_PARENTH)
                )
            )
            .accepts(context);
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        FluidInlineStatement expression = (FluidInlineStatement) PsiTreeUtil.findFirstParent(context, psiElement -> psiElement instanceof FluidInlineStatement);
        if (expression == null) {
            return;
        }

        Template tagTemplate = LiveTemplateFactory.createTagModeForLoopTemplate(expression);
        tagTemplate.addVariable("EACH", new TextExpression(expression.getText()), true);
        tagTemplate.addVariable("AS", new TextExpression("myVar"), true);

        int textOffset = expression.getTextOffset();
        editor.getCaretModel().moveToOffset(textOffset);

        TemplateManager.getInstance(context.getProject()).startTemplate(editor, tagTemplate);
        PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());

        expression.delete();
    }
}
