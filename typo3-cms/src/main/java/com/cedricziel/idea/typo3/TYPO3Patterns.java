package com.cedricziel.idea.typo3;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

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

    public static boolean iconAPIIconRetrieval(@NotNull PsiElement element) {
        if (!PlatformPatterns
            .and(
                stringLeafElementPattern(),
                PlatformPatterns.psiElement().withSuperParent(
                3, PlatformPatterns.psiElement(MethodReference.class)
                )
            )
            .accepts(element)) {

            return false;
        }


        MethodReference methodReference = (MethodReference) PsiTreeUtil.findFirstParent(element, e -> e instanceof MethodReference);

        return methodReference.getName().equals("getIcon");
    }

    @NotNull
    private static ElementPattern<PsiElement> stringLeafElementPattern() {

        return PlatformPatterns.or(
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE).withParent(StringLiteralExpression.class),
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL).withParent(StringLiteralExpression.class)
        );
    }
}
