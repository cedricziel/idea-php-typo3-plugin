package com.cedricziel.idea.typo3.tca;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TCAPatterns {
    @Contract(pure = true)
    public static boolean isWizard(@NotNull ArrayCreationExpression expression) {

        return isWizard().accepts(expression);
    }

    @Contract(pure = true)
    public static PsiElementPattern.Capture<PsiElement> isWizard() {

        return PlatformPatterns.psiElement()
                .withSuperParent(5, arrayKeyWithString("wizards", PhpElementTypes.ARRAY_KEY));
    }

    public static boolean hasArrayHashElement(@NotNull ArrayCreationExpression expression, @NotNull String arrayKey) {

        return hasArrayHashElementPattern(arrayKey, null).accepts(expression);
    }

    public static boolean hasArrayHashElement(@NotNull ArrayCreationExpression expression, @NotNull String arrayKey, String arrayValue) {

        return hasArrayHashElementPattern(arrayKey, arrayValue).accepts(expression);
    }

    public static ElementPattern<PsiElement> hasArrayHashElementPattern(@NotNull String arrayKey) {
        return hasArrayHashElementPattern(arrayKey, null);
    }

    public static ElementPattern<PsiElement> hasArrayHashElementPattern(@NotNull String arrayKey, String arrayValue) {

        if (arrayValue != null) {

            return PlatformPatterns
                    .and(
                            PlatformPatterns.psiElement().withChild(
                                    arrayKeyWithString(arrayKey, PhpElementTypes.ARRAY_KEY)
                            ),
                            PlatformPatterns.psiElement().withChild(
                                    arrayKeyWithString(arrayValue, PhpElementTypes.ARRAY_VALUE)
                            )
                    );

        }

        return PlatformPatterns.psiElement().withChild(
                arrayKeyWithString(arrayKey, PhpElementTypes.ARRAY_KEY)
        );
    }

    private static PsiElementPattern.Capture<ArrayHashElement> arrayKeyWithString(@NotNull String arrayKey, IElementType arrayKey2) {
        return PlatformPatterns.psiElement(ArrayHashElement.class).withChild(
                elementWithStringLiteral(arrayKey2, arrayKey)
        );
    }

    @NotNull
    public static ElementPattern<PsiElement> elementWithStringLiteral(IElementType elementType, @NotNull String value) {

        return PlatformPatterns.or(
                PlatformPatterns.psiElement(elementType).withText("\"" + value + "\""),
                PlatformPatterns.psiElement(elementType).withText("'" + value + "'")
        );
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> isEvalColumnValue() {
        return arrayAssignmentValueWithIndexPattern("eval");
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> arrayAssignmentValueWithIndexPattern(@NotNull String targetIndex) {

        return PlatformPatterns.psiElement().withParent(
                PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                        PlatformPatterns.or(
                                // ['eval' => '<caret>']
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                                        PlatformPatterns.psiElement(ArrayHashElement.class).withChild(
                                                elementWithStringLiteral(PhpElementTypes.ARRAY_KEY, targetIndex)
                                        )
                                ),
                                // $GLOBALS['eval'] = '<caret>';
                                PlatformPatterns.psiElement(PhpElementTypes.ASSIGNMENT_EXPRESSION).withChild(
                                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_ACCESS_EXPRESSION).withChild(
                                                elementWithStringLiteral(PhpElementTypes.ARRAY_INDEX, targetIndex)
                                        )
                                )
                        )
                )
        );
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> arrayAssignmentIndexWithIndexPattern(@NotNull String targetIndex) {

        return PlatformPatterns.psiElement().withParent(
                PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                        PlatformPatterns.or(
                                // ['config' => ['<caret>']]
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_CREATION_EXPRESSION).withParent(
                                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                                                        PlatformPatterns.psiElement(ArrayHashElement.class).withChild(
                                                                elementWithStringLiteral(PhpElementTypes.ARRAY_KEY, targetIndex)
                                                        )
                                                )
                                        )
                                ),
                                // ['config' => ['<caret>' => '']]
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withParent(
                                        PlatformPatterns.psiElement(ArrayHashElement.class).withParent(
                                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_CREATION_EXPRESSION).withParent(
                                                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                                                                PlatformPatterns.psiElement(ArrayHashElement.class).withChild(
                                                                        elementWithStringLiteral(PhpElementTypes.ARRAY_KEY, targetIndex)
                                                                )
                                                        )
                                                )
                                        )
                                ),
                                // $GLOBALS['<caret>'] = '';
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_INDEX).withParent(
                                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_ACCESS_EXPRESSION).withChild(
                                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_ACCESS_EXPRESSION).withChild(
                                                        elementWithStringLiteral(PhpElementTypes.ARRAY_INDEX, targetIndex)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @NotNull
    public static ElementPattern<? extends PsiElement> isIndexInParentIndex(@NotNull String targetParentIndex) {

        return arrayAssignmentIndexWithIndexPattern(targetParentIndex);
    }
}
