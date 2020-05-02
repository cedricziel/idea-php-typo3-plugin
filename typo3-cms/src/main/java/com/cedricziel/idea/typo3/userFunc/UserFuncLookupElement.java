package com.cedricziel.idea.typo3.userFunc;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndexKey;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UserFuncLookupElement extends PhpLookupElement {
    public UserFuncLookupElement(@NotNull String name, @NotNull StubIndexKey indexKey, @NotNull Icon ico, PhpType phpType, @NotNull Project project, InsertHandler handler) {
        super(name, indexKey, ico, phpType, project, handler);
    }

    public UserFuncLookupElement(@NotNull String name, @NotNull StubIndexKey indexKey, @NotNull Project project, InsertHandler handler) {
        super(name, indexKey, project, handler);
    }

    public UserFuncLookupElement(@NotNull PhpNamedElement namedElement) {
        super(namedElement);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        UserFuncInsertHandler.getInstance().handleInsert(context, this);
    }
}
