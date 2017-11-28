package com.cedricziel.idea.typo3.routing;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

public class RouteReferenceProvider extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (isGenerator(element)) {
                            return new PsiReference[]{new RouteReference((StringLiteralExpression) element)};
                        }

                        return new PsiReference[0];
                    }
                }
        );
    }

    private boolean isGenerator(@NotNull PsiElement element) {
        if (!PlatformPatterns
                .psiElement(StringLiteralExpression.class).withParent(
                        PlatformPatterns.psiElement(ParameterList.class).withParent(
                                PlatformPatterns.psiElement(MethodReference.class))
                )
                .accepts(element)) {

            return false;
        }

        PsiElement methodRef = element.getParent().getParent();
        if (methodRef instanceof MethodReference) {
            Method method = (Method) ((MethodReference) methodRef).resolve();
            if (method != null) {
                return isClassMethodCombination(method, "\\TYPO3\\CMS\\Backend\\Routing\\UriBuilder", "buildUriFromRoutePath")
                        || isClassMethodCombination(method, "\\TYPO3\\CMS\\Backend\\Routing\\UriBuilder", "buildUriFromRoute")
                        || isClassMethodCombination(method, "\\TYPO3\\CMS\\Backend\\Routing\\UriBuilder", "buildUriFromAjaxId")
                        || isClassMethodCombination(method, "\\TYPO3\\CMS\\Backend\\Utility\\BackendUtility", "getAjaxUrl");
            }
        }

        return false;
    }

    private boolean isClassMethodCombination(@NotNull Method method, @NotNull String className, @NotNull String methodName) {
        if (method.getName().equals(methodName)) {
            PhpClass containingClass = method.getContainingClass();
            if (containingClass != null && containingClass.getFQN().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
