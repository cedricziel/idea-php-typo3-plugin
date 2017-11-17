package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TranslationCompletionContributor extends CompletionContributor {
    public TranslationCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(StringLiteralExpression.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        Project project = position.getProject();
                        PsiElement parent = position.getParent();

                        String currentText = parent.getText();
                        if (project.getBasePath() == null || !currentText.contains("LLL:")) {
                            return;
                        }

                        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, project);
                        allKeys
                                .parallelStream()
                                .forEach(id -> {
                            LookupElementBuilder element = LookupElementBuilder
                                    .create(id)
                                    .bold()
                                    .withPresentableText(id.split(":")[3])
                                    .withTailText(id.split(":")[2], true);
                            result.addElement(element);
                        });
                    }
                }
        );
    }
}
