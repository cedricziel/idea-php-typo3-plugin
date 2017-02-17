package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * TypeProvider for `GeneralUtility::makeInstance`
 */
public class GeneralUtilityTypeProvider implements PhpTypeProvider2 {

    @Override
    public char getKey() {
        return '\u0205';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (!(psiElement instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(psiElement, "makeInstance")) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        if (methodReference.getParameters().length == 0) {
            return null;
        }

        PsiElement firstParam = methodReference.getParameters()[0];

        if (firstParam instanceof PhpReference) {
            PhpReference ref = (PhpReference) firstParam;
            if (ref.getText().toLowerCase().contains("::class")) {
                return methodReference.getSignature() + "%" + ref.getSignature();
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        int endIndex = s.lastIndexOf("%");
        if (endIndex == -1) {
            return Collections.emptySet();
        }

        // Get FQN from parameter string.
        // Example (PhpStorm 8): #K#C\Foo\Bar::get()%#K#C\Bar\Baz. -> \Bar\Baz.
        // Example (PhpStorm 9): #K#C\Foo\Bar::get()%#K#C\Bar\Baz.class -> \Bar\Baz.class
        String parameter = s.substring(endIndex + 5, s.length());

        if (parameter.contains(".class")) { // for PhpStorm 9
            parameter = parameter.replace(".class", "");
        }

        if (parameter.contains(".")) {
            parameter = parameter.replace(".", "");
        }

        return PhpIndex.getInstance(project).getAnyByFQN(parameter);
    }
}
