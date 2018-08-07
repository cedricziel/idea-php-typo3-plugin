package com.cedricziel.idea.fluid.util;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import org.jetbrains.annotations.NotNull;

public class FluidTypeInsertHandler implements InsertHandler<LookupElement> {

    private static final FluidTypeInsertHandler instance = new FluidTypeInsertHandler();

    public static FluidTypeInsertHandler getInstance() {
        return instance;
    }

    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {

        PsiElement psiElement = lookupElement.getPsiElement();
        if (psiElement instanceof Method && needParameter((Method) psiElement)) {
            if (PhpInsertHandlerUtil.isStringAtCaret(context.getEditor(), "(")) {
                return;
            }

            String addText = "()";
            PhpInsertHandlerUtil.insertStringAtCaret(context.getEditor(), addText);

            context.getEditor().getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
        }
    }

    private boolean needParameter(Method method) {

        for (Parameter parameter : method.getParameters()) {
            if (!parameter.isOptional()) {
                return true;
            }
        }

        return false;
    }
}
