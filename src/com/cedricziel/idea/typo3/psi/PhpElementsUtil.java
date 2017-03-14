package com.cedricziel.idea.typo3.psi;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;

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

    /**
     * Checks whether subject extends extendedClass.
     *
     * @param subject       The class to check
     * @param extendedClass The class to look for in the inheritance chain
     * @return Whether the subject extends extendedClass
     */
    public static boolean extendsClass(PhpClass subject, PhpClass extendedClass) {

        List<ClassReference> classReferences = allExtendedClasses(subject)
                .stream()
                .map(e -> (ClassReference) e.getReference())
                .collect(Collectors.toList());

        return classReferences.contains((ClassReference) extendedClass.getReference());
    }

    /**
     * Retrieves a list of all classes in the inheritance chain.
     *
     * @param phpClass The class to investigate
     * @return List of classes that phpClass inherits from
     */
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
}
