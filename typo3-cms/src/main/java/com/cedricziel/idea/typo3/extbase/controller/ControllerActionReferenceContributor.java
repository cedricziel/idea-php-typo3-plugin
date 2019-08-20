package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.extbase.ExtbasePatterns;
import com.cedricziel.idea.typo3.extbase.ExtbaseUtils;
import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

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
                        return PsiReference.EMPTY_ARRAY;
                    }

                    StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;

                    String methodName = PhpLangUtil.getMethodName(element);
                    int parameterPosition = PhpLangUtil.getParameterPosition(stringLiteralExpression);
                    if (methodName != null && ExtbaseUtils.isDirectingActionName(methodName) && parameterPosition == 0) {
                        return new PsiReference[]{new ControllerActionReference(stringLiteralExpression)};
                    }

                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );

        // register plugin
        registrar.registerReferenceProvider(
            ExtbasePatterns.configurePluginActionPattern(),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    if (!(element instanceof StringLiteralExpression)) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;
                    ArrayHashElement arrayHashElement = PsiTreeUtil.getParentOfType(element, ArrayHashElement.class);

                    if (arrayHashElement != null && arrayHashElement.getKey() instanceof ClassConstantReference) {
                        final ClassReference key = (ClassReference) ((ClassConstantReference) arrayHashElement.getKey()).getClassReference();
                        return referenceClassMethodsOnString(key.getFQN(), stringLiteralExpression);
                    }

                    // guard against unsupported cases
                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );
    }

    private PsiReference[] referenceClassMethodsOnString(String fqn, StringLiteralExpression stringLiteralExpression) {
        Collection<PsiReference> collected = new ArrayList<>();

        final String contents = stringLiteralExpression.getContents();
        String[] methodNames = contents.trim().split(",");
        if (methodNames.length <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }

        final Collection<PhpClass> classesByFQN = PhpIndex.getInstance(stringLiteralExpression.getProject()).getClassesByFQN(fqn);
        for (PhpClass phpClass : classesByFQN) {
            for(String methodName: methodNames) {
                final Method methodByName = phpClass.findMethodByName(methodName.trim() + "Action");
                if (methodByName != null) {
                    final TextRange textRange = new TextRange(contents.indexOf(methodName) + 1, contents.indexOf(methodName) + methodName.length() + 1);

                    collected.add(new ControllerActionReference(stringLiteralExpression, textRange));
                }
            }
        }

        return collected.toArray(new PsiReference[0]);
    }
}
