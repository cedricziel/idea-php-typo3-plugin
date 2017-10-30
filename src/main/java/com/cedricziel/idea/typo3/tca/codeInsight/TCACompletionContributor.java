package com.cedricziel.idea.typo3.tca.codeInsight;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.cedricziel.idea.typo3.util.TableUtil.TCA_TABLE_FIELDS;

public class TCACompletionContributor extends CompletionContributor {

    public TCACompletionContributor() {
        /*
         * Complete table names in array values.
         */
        extend(
                CompletionType.BASIC,
                PhpElementsUtil.isStringArrayValue(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();

                        // ArrayHashElement
                        PsiElement arrayElement = element.getParent().getParent().getParent();
                        if (arrayElement instanceof ArrayHashElement) {
                            ArrayHashElement arrayHashElement = (ArrayHashElement) arrayElement;
                            PhpPsiElement keyPsiElement = arrayHashElement.getKey();
                            if (keyPsiElement instanceof StringLiteralExpression) {
                                String key = ((StringLiteralExpression) keyPsiElement).getContents();

                                if (Arrays.asList(TCA_TABLE_FIELDS).contains(key)) {
                                    TableUtil.completeAvailableTableNames(element.getProject(), result);
                                }
                            }
                        }
                    }
                }
        );
    }
}
