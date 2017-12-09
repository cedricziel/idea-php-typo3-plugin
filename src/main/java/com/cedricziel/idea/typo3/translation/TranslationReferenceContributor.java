package com.cedricziel.idea.typo3.translation;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class TranslationReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                if (!(element instanceof StringLiteralExpression)) {
                    return new PsiReference[0];
                }

                StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;
                if (!stringLiteralExpression.getContents().startsWith("LLL:")) {
                    return new PsiReference[0];
                }

                return new PsiReference[]{new TranslationReference(stringLiteralExpression)};
            }
        });
    }
}
