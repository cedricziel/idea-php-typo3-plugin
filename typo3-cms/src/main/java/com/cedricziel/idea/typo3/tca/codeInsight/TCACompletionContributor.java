package com.cedricziel.idea.typo3.tca.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.tca.TCAPatterns;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class TCACompletionContributor extends CompletionContributor {

    public TCACompletionContributor() {
        /*
         * Complete 'renderType' fields in TCA
         */
        extend(
                CompletionType.BASIC,
                TCAPatterns.arrayAssignmentValueWithIndexPattern("renderType"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        for (String renderType: TCAUtil.getAvailableRenderTypes(parameters.getPosition())) {
                            result.addElement(LookupElementBuilder.create(renderType).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                        }
                    }
                }
        );

        /*
         * Complete 'type' fields in TCA
         */
        extend(
                CompletionType.BASIC,
                TCAPatterns.arrayAssignmentValueWithIndexPattern("type"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        for (String columnType: TCAUtil.getAvailableColumnTypes(parameters.getPosition().getProject())) {
                            result.addElement(LookupElementBuilder.create(columnType).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                        }
                    }
                }
        );

        /*
         * Complete available 'eval' values in TCA
         */
        extend(
                CompletionType.BASIC,
                TCAPatterns.isEvalColumnValue(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

                        StringLiteralExpression literalExpression = (StringLiteralExpression) parameters.getOriginalPosition().getParent();
                        if (literalExpression == null) {
                            return;
                        }

                        for (String evaluationName: TCAUtil.getAvailableEvaluations(parameters.getPosition().getProject())) {
                            result.addElement(LookupElementBuilder.create(evaluationName).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                        }

                        int lastIndexOf = StringUtils.lastIndexOf(literalExpression.getContents(), ",");
                        if (lastIndexOf != -1 && literalExpression.getContents().length() == lastIndexOf + 1) {
                            for (String evaluationName: TCAUtil.getAvailableEvaluations(parameters.getPosition().getProject())) {
                                LookupElementBuilder element = LookupElementBuilder.create(literalExpression.getContents() + evaluationName)
                                        .withPresentableText(evaluationName)
                                        .withIcon(TYPO3CMSIcons.TYPO3_ICON);

                                result.addElement(element);
                            }
                        }

                        if (lastIndexOf != -1 && literalExpression.getContents().length() >= lastIndexOf + 1) {
                            for (String evaluationName: TCAUtil.getAvailableEvaluations(parameters.getPosition().getProject())) {
                                String newExpression = literalExpression.getContents().substring(0, lastIndexOf) + "," + evaluationName;
                                LookupElementBuilder element = LookupElementBuilder.create(newExpression)
                                        .withPresentableText(evaluationName)
                                        .withIcon(TYPO3CMSIcons.TYPO3_ICON);

                                result.addElement(element);
                            }
                        }
                    }
                }
        );

        /*
         * Complete available 'config' options in TCA
         */
        extend(
                CompletionType.BASIC,
                TCAPatterns.isIndexInParentIndex("config"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        for (String evaluationName: TCAUtil.getConfigSectionChildren()) {
                            result.addElement(LookupElementBuilder.create(evaluationName).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                        }
                    }
                }
        );
    }
}
