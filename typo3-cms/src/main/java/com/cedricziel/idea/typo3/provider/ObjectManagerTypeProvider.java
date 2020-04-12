package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.PhpTypeProviderUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.Nullable;

/**
 * TypeProvider for `ObjectManager::get`
 */
public class ObjectManagerTypeProvider extends AbstractServiceLocatorTypeProvider {

    @Override
    public char getKey() {
        return '\u0215';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb() || !TYPO3CMSProjectSettings.isEnabled(psiElement)) {
            return null;
        }

        if (!(psiElement instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(psiElement, "get")) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        String signature = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(methodReference, TRIM_KEY);
        return  signature == null ? null : new PhpType().add("#" + getKey() + signature);
    }
}
