package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.util.TYPO3Utility;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ContextReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression.class).withSuperParent(2,
                PlatformPatterns.psiElement(MethodReference.class)
            ),
            new ContextReferenceProvider()
        );
    }

    private static class ContextReferenceProvider extends PsiReferenceProvider {
        @NotNull
        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            MethodReference methodReference = (MethodReference) PsiTreeUtil.findFirstParent(element, m -> m instanceof MethodReference);
            if (methodReference == null || methodReference.getName() == null || !methodReference.getName().equals("getAspect")) {
                return PsiReference.EMPTY_ARRAY;
            }

            Method m = (Method) methodReference.resolve();
            if (m == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            PhpClass c = m.getContainingClass();
            if (c == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            if (!c.getFQN().equals(TYPO3Utility.CONTEXT_FQN)) {
                return PsiReference.EMPTY_ARRAY;
            }

            return new PsiReference[]{
                new ContextReference(element)
            };
        }
    }
}
