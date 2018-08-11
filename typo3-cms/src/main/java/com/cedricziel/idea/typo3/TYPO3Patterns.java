package com.cedricziel.idea.typo3;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class TYPO3Patterns {
    /*
     * \TYPO3\CMS\Extbase\SignalSlot\Dispatcher::class->connect(,,,'<caret>')
     */
    public static ElementPattern<? extends PsiElement> connectSignalMethodName() {
        return PlatformPatterns
            .psiElement().withParent(
                PlatformPatterns.psiElement(StringLiteralExpression.class)
            )
            .withSuperParent(3, PlatformPatterns.psiElement(MethodReference.class));
    }

    public static ElementPattern<? extends PsiElement> connectSignalMethodNameString() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .withSuperParent(2, PlatformPatterns.psiElement(MethodReference.class));
    }
}
