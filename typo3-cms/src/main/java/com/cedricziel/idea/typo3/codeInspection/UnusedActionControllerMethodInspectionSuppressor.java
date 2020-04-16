package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnusedActionControllerMethodInspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!toolId.equals("PhpUnused")) {
            return false;
        }

        if (isActionMethod(element)) {
            PsiElement classParent = PsiTreeUtil.findFirstParent(element, e -> e instanceof PhpClass);
            if (classParent == null) {
                return false;
            }

            return isControllerPhpClass((PhpClass) classParent);
        }

        if (isControllerClassElement(element)) {
            return isControllerPhpClass((PhpClass) element);
        }

        return false;
    }

    private boolean isControllerClassElement(@NotNull PsiElement element) {
        return PlatformPatterns.psiElement(PhpClass.class).withName(PlatformPatterns.string().endsWith("Controller")).accepts(element);
    }

    private boolean isControllerPhpClass(PhpClass classParent) {

        return PhpIndex.getInstance(classParent.getProject())
            .getAllSubclasses("TYPO3\\CMS\\Extbase\\Mvc\\Controller\\ControllerInterface")
            .contains(classParent);
    }

    private boolean isActionMethod(PsiElement element) {

        return PlatformPatterns.psiElement(Method.class)
            .withName(PlatformPatterns.string().endsWith("Action"))
            .accepts(element);
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
