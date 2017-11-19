package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class PathResourceCompletionContributor extends CompletionContributor {
    public PathResourceCompletionContributor() {
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
                        if (project.getBasePath() == null || !currentText.contains("EXT:")) {
                            return;
                        }

                        ResourcePathIndex.getAvailableExtensionResourceFiles(project).forEach(identifier -> {
                            result.addElement(LookupElementBuilder.create(identifier));
                        });
                    }
                }
        );
    }
}
