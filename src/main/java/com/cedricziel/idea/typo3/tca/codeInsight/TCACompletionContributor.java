package com.cedricziel.idea.typo3.tca.codeInsight;

import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TCACompletionContributor extends CompletionContributor {

    private static final String[] TCA_TABLE_FIELDS = {
            "foreign_table",
            "allowed",
    };

    public TCACompletionContributor() {
        /*
         * Complete table names in array values.
         */
        extend(
                CompletionType.BASIC,
                PhpPatterns.psiElement().withParent(
                        PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE))
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();

                        // ArrayHashElement
                        PsiElement arrayElement = position.getParent().getParent().getParent();
                        if (arrayElement instanceof ArrayHashElement) {
                            ArrayHashElement arrayHashElement = (ArrayHashElement) arrayElement;
                            PhpPsiElement keyPsiElement = arrayHashElement.getKey();
                            if (keyPsiElement instanceof StringLiteralExpression) {
                                String key = ((StringLiteralExpression) keyPsiElement).getContents();

                                if (Arrays.asList(TCA_TABLE_FIELDS).contains(key)) {
                                    TableUtil.completeAvailableTableNames(position.getProject(), result);
                                }
                            }
                        }
                    }
                });
    }
}
