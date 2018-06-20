package com.cedricziel.idea.typo3.extbase;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ExtbasePatterns {
    @NotNull
    public static PsiElementPattern.Capture<PsiElement> stringArgumentOnMethodCallPattern() {
        return PlatformPatterns.psiElement()
                .withParent(
                        PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                                PlatformPatterns.psiElement(ParameterList.class).withParent(
                                        PlatformPatterns.psiElement(MethodReference.class)
                                )
                        )
                );
    }
}
