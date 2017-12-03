package com.cedricziel.idea.typo3.resources;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ResourceReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // "EXT:" strings
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression stringLiteralExpression = (StringLiteralExpression)element;
                        if (!stringLiteralExpression.getContents().startsWith("EXT:")) {
                            return new PsiReference[0];
                        }

                        return new PsiReference[]{new ResourceReference(stringLiteralExpression)};
                    }
                }
        );
    }
}
