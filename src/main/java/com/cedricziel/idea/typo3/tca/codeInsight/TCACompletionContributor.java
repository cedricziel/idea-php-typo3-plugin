package com.cedricziel.idea.typo3.tca.codeInsight;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.arrayIndexIsTCATableNameField;

public class TCACompletionContributor extends CompletionContributor {

    public TCACompletionContributor() {
        /*
         * Complete 'renderType' fields in TCA
         */
        extend(
                CompletionType.BASIC,
                PhpElementsUtil.isStringArrayValue(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();

                        String arrayIndex = extractArrayIndexFromValue(element);
                        if ("renderType".equals(arrayIndex)) {
                            for(String name: TCAUtil.getAvailableRenderTypes(element)) {
                                result.addElement(new LookupElement() {
                                    @NotNull
                                    @Override
                                    public String getLookupString() {
                                        return name;
                                    }
                                });
                            }
                        }
                    }
                }
        );

        /*
         * Complete 'type' fields in TCA
         */
        extend(
                CompletionType.BASIC,
                PhpElementsUtil.isStringArrayValue(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();

                        String arrayIndex = extractArrayIndexFromValue(element);
                        if ("type".equals(arrayIndex)) {
                            for(String name: TCAUtil.getAvailableColumnTypes(element)) {
                                result.addElement(new LookupElement() {
                                    @NotNull
                                    @Override
                                    public String getLookupString() {
                                        return name;
                                    }
                                });
                            }
                        }
                    }
                }
        );
    }
}
