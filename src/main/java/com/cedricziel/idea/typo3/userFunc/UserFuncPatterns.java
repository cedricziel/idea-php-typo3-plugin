package com.cedricziel.idea.typo3.userFunc;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class UserFuncPatterns {
    public static boolean expectUserFuncReferenceXML(@NotNull PsiElement psiElement) {
        return expectUserFuncReferenceXMLPattern().accepts(psiElement);
    }

    public static PsiElementPattern.Capture<PsiElement> expectUserFuncReferenceXMLPattern() {
        return XmlPatterns.psiElement().withParent(
                XmlPatterns.xmlText().withParent(
                        PlatformPatterns.or(
                                XmlPatterns.xmlTag().withLocalName("userFunc"),
                                XmlPatterns.xmlTag().withName("userFunc")
                        )
                )
        );
    }

    public static ElementPattern<? extends PsiElement> expectUserFuncReferenceArrayValuePattern(@NotNull String arrayIndex) {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                        PlatformPatterns.psiElement().withChild(
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withChild(
                                        PlatformPatterns.or(
                                                PlatformPatterns.psiElement(StringLiteralExpression.class).withText("'" + arrayIndex + "'"),
                                                PlatformPatterns.psiElement(StringLiteralExpression.class).withText("\"" + arrayIndex + "\"")
                                        )
                                )
                        )
                )
        );
    }
}
