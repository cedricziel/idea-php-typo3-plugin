package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ExtbaseRepositoryMethodsInspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!toolId.equals("PhpUndefinedMethodInspection")) {
            return false;
        }

        if (!looksLikeRepositoryFinderMethod(element)) {
            return false;
        }

        MethodReference methodReference = (MethodReference) element.getParent();
        if (!(methodReference.getFirstChild() instanceof Variable)) {
            return false;
        }

        Variable variable = (Variable) methodReference.getFirstChild();
        Collection<? extends PhpNamedElement> bySignature1 = PhpIndex.getInstance(element.getProject()).getBySignature(variable.getSignature());
        List<PhpClass> repositories = bySignature1
            .stream()
            .filter(x -> x instanceof PhpClass)
            .filter(x -> isRepositoryClass((PhpClass) x))
            .map(x -> (PhpClass)x)
            .collect(Collectors.toList());

        return !repositories.isEmpty();
    }

    private boolean isRepositoryClass(PhpClass containingClass) {
        return PhpIndex.getInstance(containingClass.getProject())
            .getAllSubclasses("TYPO3\\CMS\\Extbase\\Persistence\\RepositoryInterface")
            .contains(containingClass);
    }

    private boolean looksLikeRepositoryFinderMethod(PsiElement element) {

        return PlatformPatterns.psiElement()
            .withText(
                PlatformPatterns.or(
                    PlatformPatterns.string().startsWith("find"),
                    PlatformPatterns.string().startsWith("count")
                )
            )
            .withParent(
                PlatformPatterns.psiElement(MethodReference.class)
            )
            .accepts(element);
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
