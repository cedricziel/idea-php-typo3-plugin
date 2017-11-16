package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;

public class IconCompletionContributor extends CompletionContributor {
    public IconCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement element = parameters.getPosition().getParent();
                        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
                        if (parameterList == null) {
                            return;
                        }

                        PsiElement methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
                        if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getIcon")) {
                            completeIconsFromIndex(element, resultSet);
                        }
                    }
                }
        );
    }

    private void completeIconsFromIndex(PsiElement element, CompletionResultSet resultSet) {
        IconIndex
                .getAllAvailableIcons(element.getProject())
                .forEach(iconIdentifier -> resultSet.addElement(LookupElementBuilder.create(iconIdentifier).withIcon(TYPO3CMSIcons.ICON_NOT_RESOLVED)));
    }
}
