package com.cedricziel.idea.typo3.psi;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;

import java.util.Arrays;

public class PhpElementsUtil {
    public static boolean isMethodWithFirstStringOrFieldReference(PsiElement psiElement, String... methodName) {
        if(!PlatformPatterns
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
}
