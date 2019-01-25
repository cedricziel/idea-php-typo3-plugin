package com.cedricziel.idea.typo3.userFunc;

import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class UserFuncPatterns {
    @NotNull
    public static PsiElementPattern.Capture<PsiElement> expectUserFuncReferenceXMLPattern() {
        return XmlPatterns.psiElement().withParent(
                XmlPatterns.xmlText().withParent(
                        PlatformPatterns.or(
                                XmlPatterns.xmlTag().withLocalName("userFunc"),
                                XmlPatterns.xmlTag().withName("userFunc"),
                                XmlPatterns.xmlTag().withLocalName("itemsProcFunc"),
                                XmlPatterns.xmlTag().withName("itemsProcFunc")
                        )
                )
        );
    }

    @NotNull
    public static ElementPattern<? extends PsiElement> expectUserFuncReferenceArrayValuePattern(@NotNull String arrayIndex) {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                expectArrayKeyForValue(arrayIndex)
        );
    }

    @NotNull
    public static ElementPattern<? extends PsiElement> expectUserFuncReferenceStringConcatArrayValuePattern(@NotNull String arrayIndex) {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                PlatformPatterns.psiElement(ConcatenationExpression.class).withParent(
                        expectArrayKeyForValue(arrayIndex)
                )
        );
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> expectArrayKeyForValue(@NotNull String arrayIndex) {
        return PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                PlatformPatterns.psiElement().withChild(
                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withChild(
                                PlatformPatterns.or(
                                        PlatformPatterns.psiElement(StringLiteralExpression.class).withText("'" + arrayIndex + "'"),
                                        PlatformPatterns.psiElement(StringLiteralExpression.class).withText("\"" + arrayIndex + "\"")
                                )
                        )
                )
        );
    }
}
