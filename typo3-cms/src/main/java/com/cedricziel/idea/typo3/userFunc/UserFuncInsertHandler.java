package com.cedricziel.idea.typo3.userFunc;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import org.jetbrains.annotations.NotNull;

public class UserFuncInsertHandler implements InsertHandler<UserFuncLookupElement> {
    private static final UserFuncInsertHandler INSTANCE = new UserFuncInsertHandler();

    public static UserFuncInsertHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull UserFuncLookupElement item) {

    }
}
