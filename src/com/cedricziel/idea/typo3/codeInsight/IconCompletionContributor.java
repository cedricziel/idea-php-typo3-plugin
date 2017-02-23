package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.IconUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

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
                            completeAvailableIcons(element, resultSet);
                        }
                    }
                }
        );
    }

    private void completeAvailableIcons(PsiElement element, CompletionResultSet resultSet) {
        IconProvider iconProvider = IconProvider.getInstance(element.getProject());
        iconProvider.all().forEach(iconDefinition -> {
            LookupElementBuilder lookupElement;
            try {
                if (iconDefinition.getVirtualFile() == null) {
                    throw new IOException("Could not find icon source");
                }
                Icon iconFromFile = IconUtil.createIconFromFile(iconDefinition.getVirtualFile());
                if (iconFromFile == null) {
                    throw new IOException("Unable to create image from icon source");
                }
                lookupElement = LookupElementBuilder
                        .create(iconDefinition.getName())
                        .withIcon(iconFromFile);
            } catch (IOException e) {
                // Silent error
                lookupElement = LookupElementBuilder
                        .create(iconDefinition.getName())
                        .withIcon(TYPO3CMSIcons.ROUTE_ICON);
            }

            if (iconDefinition.getFilename() != null) {
                lookupElement = lookupElement.appendTailText(iconDefinition.getFilename(), true);
            } else if (iconDefinition.getProvider() != null){
                lookupElement = lookupElement.appendTailText(iconDefinition.getProvider().getText(), true);
            }

            resultSet.addElement(lookupElement);
        });
    }
}
