package com.cedricziel.idea.typo3.resources;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLQuotedText;

public class ResourceReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // PHP: "EXT:" strings
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression stringLiteralExpression = (StringLiteralExpression)element;
                        if (!stringLiteralExpression.getContents().startsWith("EXT:") && !stringLiteralExpression.getContents().startsWith("LLL:EXT:")) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        return new PsiReference[]{new ResourceReference(stringLiteralExpression)};
                    }
                }
        );

        // YAML: "EXT:" strings
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(YAMLQuotedText.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        YAMLQuotedText yamlQuotedText = (YAMLQuotedText)element;
                        if (!yamlQuotedText.getTextValue().startsWith("EXT:") && !yamlQuotedText.getTextValue().startsWith("LLL:EXT:")) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        return new PsiReference[]{new ResourceReference(yamlQuotedText)};
                    }
                }
        );
    }
}
