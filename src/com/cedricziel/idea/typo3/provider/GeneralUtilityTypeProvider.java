package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.Nullable;

/**
 * TypeProvider for `GeneralUtility::makeInstance`
 */
public class GeneralUtilityTypeProvider extends AbstractServiceLocatorTypeProvider {

    @Override
    public char getKey() {
        return '\u0205';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
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
                return new PhpType().add(methodReference.getSignature() + "%" + ref.getSignature());
            }
        }

        return null;
    }
}
