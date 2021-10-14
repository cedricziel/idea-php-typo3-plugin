package com.cedricziel.idea.typo3.flexform;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FlexFormCompletionContributionProvider extends CompletionContributor {
    public FlexFormCompletionContributionProvider() {
        // complete table names in flexforms
        extend(CompletionType.BASIC, parentWithName("config", "table", "foreign_table"), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                final LookupElement[] availableTableNamesLookupElements = TableUtil.createAvailableTableNamesLookupElements(parameters.getPosition().getProject());
                result.addAllElements(
                    Arrays.asList(
                        availableTableNamesLookupElements
                    )
                );
            }
        });

        // complete field types
        extend(CompletionType.BASIC, parentWithName("config", "type"), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                for (String columnType : TCAUtil.getAvailableColumnTypes(parameters.getPosition().getProject())) {
                    result.addElement(LookupElementBuilder.create(columnType).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                }
            }
        });

        // complete field render types
        extend(CompletionType.BASIC, parentWithName("config", "renderType"), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                for (String columnType : TCAUtil.getAvailableRenderTypes(parameters.getPosition())) {
                    result.addElement(LookupElementBuilder.create(columnType).withIcon(TYPO3CMSIcons.TYPO3_ICON));
                }
            }
        });
    }

    @NotNull
    private static PsiElementPattern.Capture<PsiElement> parentWithName(@NotNull String parentTag, @NotNull String... names) {
        return XmlPatterns
            .psiElement(XmlTokenType.XML_DATA_CHARACTERS)
            .withParent(
                XmlPatterns.xmlText().withParent(
                    XmlPatterns.xmlTag().withName(XmlPatterns.string().oneOf(names))
                )
            ).inside(XmlPatterns.xmlTag().withName(parentTag));
    }
}
