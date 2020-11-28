package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.cedricziel.idea.fluid.codeInsight.template.LiveTemplateFactory;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldChain;
import com.cedricziel.idea.fluid.lang.psi.FluidInlineStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperExpr;
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

public class AliasPostfixTemplate extends PostfixTemplate {
    protected AliasPostfixTemplate(FluidPostfixTemplateProvider fluidPostfixTemplateProvider) {
        super("f:alias", "alias","Alias the current expression", fluidPostfixTemplateProvider);
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

        Template tagTemplate = LiveTemplateFactory.createTagModeAliasTemplate(expression);
        tagTemplate.addVariable("ALIAS", new TextExpression("myVar"), true);
        tagTemplate.addVariable("EXPR", new TextExpression("'" + expression.getText() + "'"), true);

        int textOffset = expression.getTextOffset() + expression.getTextLength();
        editor.getCaretModel().moveToOffset(textOffset);

        TemplateManager.getInstance(context.getProject()).startTemplate(editor, tagTemplate);
        PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());

        expression.delete();
    }
}
