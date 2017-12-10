package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerActionReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // follow, redirect
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof StringLiteralExpression)) {
                            return new PsiReference[0];
                        }

                        StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;

                        String methodName = PhpLangUtil.getMethodName(element);
                        int parameterPosition = PhpLangUtil.getParameterPosition(stringLiteralExpression);
                        if (methodName != null && isDirectingActionName(methodName) && parameterPosition == 0) {
                            return new PsiReference[]{new ControllerActionReference(stringLiteralExpression)};
                        }

                        return new PsiReference[0];
                    }
                }
        );
    }

    private boolean isDirectingActionName(String methodName) {
        return methodName.equals("forward") || methodName.equals("redirect");
    }
}
