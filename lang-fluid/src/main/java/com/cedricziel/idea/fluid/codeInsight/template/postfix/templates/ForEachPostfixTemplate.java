package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.cedricziel.idea.fluid.lang.psi.FluidIdentifierExpr;
import com.cedricziel.idea.fluid.lang.psi.FluidInlineStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class ForEachPostfixTemplate extends PostfixTemplate {

    protected ForEachPostfixTemplate() {
        super("f:for", "Wrap element in f:for loop");
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        return PlatformPatterns.psiElement(FluidTypes.IDENTIFIER)
            .withParent(PlatformPatterns.psiElement(FluidIdentifierExpr.class))
            .accepts(context);
    }

    @Override
    public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
        StringBuilder newText = new StringBuilder();
        PsiElement parent = context.getParent();
        PsiElement child = parent.getFirstChild();

        newText.append(child.getText());
        while (child.getNextSibling() != null) {
            child = child.getNextSibling();

            newText.append(child.getText());
        }

        FluidInlineStatement fluidInlineStatement = (FluidInlineStatement) PsiTreeUtil.findFirstParent(context, psiElement -> psiElement instanceof FluidInlineStatement);
        if (fluidInlineStatement == null) {
            return;
        }

        int textOffset = fluidInlineStatement.getTextOffset();
        editor.getCaretModel().moveToOffset(textOffset);
        String foreachStatement = "<f:for each=\"" + fluidInlineStatement.getText() + "\" as=\"\">\n </f:for>";
        EditorModificationUtil.insertStringAtCaret(editor, foreachStatement);
        PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());

        fluidInlineStatement.delete();

        editor.getCaretModel().moveToOffset(textOffset + 19 + fluidInlineStatement.getTextLength());

        // CodeStyleManager.getInstance(context.getProject()).reformatText(context.getContainingFile(), textOffset, textOffset + foreachStatement.length());
    }
}
