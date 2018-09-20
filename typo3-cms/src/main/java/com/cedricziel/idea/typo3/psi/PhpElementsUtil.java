package com.cedricziel.idea.typo3.psi;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PhpElementsUtil {
    public static boolean isMethodWithFirstStringOrFieldReference(PsiElement psiElement, String... methodName) {
        if (!PlatformPatterns
                .psiElement(PhpElementTypes.METHOD_REFERENCE)
                .withChild(PlatformPatterns
                        .psiElement(PhpElementTypes.PARAMETER_LIST)
                        .withFirstChild(PlatformPatterns.or(
                                PlatformPatterns.psiElement(PhpElementTypes.STRING),
                                PlatformPatterns.psiElement(PhpElementTypes.FIELD_REFERENCE),
                                PlatformPatterns.psiElement(PhpElementTypes.CLASS_CONSTANT_REFERENCE)
                        ))
                ).accepts(psiElement)) {

            return false;
        }

        // cant we move it up to PlatformPatterns? withName condition dont looks working
        String methodRefName = ((MethodReference) psiElement).getName();

        return null != methodRefName && Arrays.asList(methodName).contains(methodRefName);
    }

    public static boolean extendsClass(PhpClass subject, PhpClass extendedClass) {

        List<ClassReference> classReferences = allExtendedClasses(subject)
                .stream()
                .map(e -> (ClassReference) e.getReference())
                .collect(Collectors.toList());

        return classReferences.contains(extendedClass.getReference());
    }

    public static List<PhpClass> allExtendedClasses(PhpClass phpClass) {
        List<PhpClass> classReferences = new ArrayList<>();
        PhpIndex index = PhpIndex.getInstance(phpClass.getProject());

        List<ClassReference> referenceElements = phpClass.getExtendsList().getReferenceElements();
        for (ClassReference reference : referenceElements) {
            Collection<PhpClass> classesByFQN = index.getClassesByFQN(reference.getFQN());
            for (PhpClass phpClass1 : classesByFQN) {
                classReferences.add(phpClass);
                classReferences.addAll(allExtendedClasses(phpClass1));
            }
        }

        return classReferences;
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> isStringArrayValue() {

        return PhpPatterns.psiElement()
                .andOr(
                        PhpPatterns.psiElement().withParent(
                                PlatformPatterns.psiElement(StringLiteralExpression.class)
                                        .withParent(PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE))
                        ),
                        PlatformPatterns.psiElement(StringLiteralExpression.class)
                                .withParent(PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE))
                );
    }

    @Nullable
    public static String extractArrayIndexFromValue(PsiElement element) {
        PsiElement arrayElement;
        if (element.getParent() instanceof StringLiteralExpression) {
            arrayElement = element.getParent().getParent().getParent();
        } else {
            arrayElement = element.getParent().getParent();
        }

        if (arrayElement instanceof ArrayHashElement) {
            ArrayHashElement arrayHashElement = (ArrayHashElement) arrayElement;
            return extractIndexFromArrayHash(arrayHashElement);
        }

        return null;
    }

    @Nullable
    private static String extractIndexFromArrayHash(ArrayHashElement arrayHashElement) {
        PhpPsiElement keyPsiElement = arrayHashElement.getKey();
        if (keyPsiElement instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) keyPsiElement).getContents();
        }
        return null;
    }

    public static boolean hasSuperClass(@NotNull PhpClass targetClass, @NotNull String parentClassName) {

        Collection<PhpClass> classesByFQN = PhpIndex.getInstance(targetClass.getProject()).getClassesByFQN(parentClassName);
        if (classesByFQN.size() == 0) {
            return false;
        }

        for (PhpClass phpClass : classesByFQN) {
            if (PhpClassHierarchyUtils.isSuperClass(phpClass, targetClass, true)) {
                return true;
            }
        }

        return false;
    }
}
