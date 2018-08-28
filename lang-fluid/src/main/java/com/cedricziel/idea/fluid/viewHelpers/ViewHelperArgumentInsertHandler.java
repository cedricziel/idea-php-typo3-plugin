package com.cedricziel.idea.fluid.viewHelpers;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil;
import org.jetbrains.annotations.NotNull;

public class ViewHelperArgumentInsertHandler implements InsertHandler<LookupElement> {
    private static final ViewHelperArgumentInsertHandler INSTANCE = new ViewHelperArgumentInsertHandler();

    public static ViewHelperArgumentInsertHandler getInstance() {
        return INSTANCE;
    }

    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {

        if (PhpInsertHandlerUtil.isStringAtCaret(context.getEditor(), ":")) {
            return;
        }

        PhpInsertHandlerUtil.insertStringAtCaret(context.getEditor(), ": ");

        context.getEditor().getCaretModel().moveCaretRelatively(0, 0, false, false, true);
    }
}
