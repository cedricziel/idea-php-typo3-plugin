package com.cedricziel.idea.typo3.injection;

import com.intellij.lang.Language;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.injection.PhpInjectorBase;
import com.jetbrains.php.lang.psi.elements.*;
import de.sgalinski.typoscript.language.TypoScriptLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypoScriptInjector extends PhpInjectorBase {

    public static final String[] TYPOSCRIPT_ARGUMENTS = new String[]{
            "TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::addPageTSConfig",
            "TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::addUserTSConfig",
            "TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::addTypoScriptSetup",
            "TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::addTypoScriptConstants",
    };

    private static boolean isTypoScriptAcceptingMethod(@NotNull PsiElement psiElement) {
        for (String TYPOSCRIPT_ARGUMENT : TYPOSCRIPT_ARGUMENTS) {
            String[] split = TYPOSCRIPT_ARGUMENT.split("::");
            if (isStringArgumentInMethodReference(psiElement) && signatureMatches(psiElement, split[0], split[1])) {

                return true;
            }
        }

        return false;
    }

    private static boolean signatureMatches(@NotNull PsiElement psiElement, @NotNull String className, @NotNull String methodName) {
        PsiElement parent = psiElement.getParent();
        PsiElement superParent = parent.getParent();

        MethodReference methodReference = (MethodReference) superParent;
        PsiElement resolve = methodReference.resolve();
        if (!(resolve instanceof Method)) {
            return false;
        }

        Method method = (Method) resolve;
        PhpClass containingClass = method.getContainingClass();

        return methodReference.getName() != null && methodReference.getName().equals(methodName)
                && containingClass != null && containingClass.getFQN().equals(className);
    }

    private static boolean isStringArgumentInMethodReference(@NotNull PsiElement psiElement) {

        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                PlatformPatterns.psiElement(ParameterList.class).withParent(
                        PlatformPatterns.psiElement(MethodReference.class)
                )
        ).accepts(psiElement);
    }

    @Nullable
    @Override
    public Language getInjectedLanguage(@NotNull PsiElement psiElement) {
        if (isTypoScriptAcceptingMethod(psiElement)) {
            return TypoScriptLanguage.INSTANCE;
        }

        return null;
    }
}
