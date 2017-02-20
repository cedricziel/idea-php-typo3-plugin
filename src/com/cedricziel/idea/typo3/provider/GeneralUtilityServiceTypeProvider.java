package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.cedricziel.idea.typo3.index.ServiceIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexImpl;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TypeProvider for `GeneralUtility::makeInstanceService`
 */
public class GeneralUtilityServiceTypeProvider extends AbstractServiceLocatorTypeProvider {

    @Override
    public char getKey() {
        return '\u0225';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (!(psiElement instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(psiElement, "makeInstanceService")) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        if (methodReference.getParameters().length == 0) {
            return null;
        }

        PsiElement firstParam = methodReference.getParameters()[0];

        if (firstParam instanceof StringLiteralExpression) {
            StringLiteralExpression ref = (StringLiteralExpression) firstParam;
            return methodReference.getSignature() + "%" + ref.getContents();
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {

        String serviceName = expression.split("%")[1];

        FileBasedIndex index = FileBasedIndexImpl.getInstance();
        List<List<TYPO3ServiceDefinition>> values = index.getValues(ServiceIndex.KEY, serviceName, GlobalSearchScope.allScope(project));
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        Collection<PhpNamedElement> phpNamedElementCollections = new ArrayList<>();

        for (List<TYPO3ServiceDefinition> serviceDefinitions : values) {
            serviceDefinitions.forEach(el -> {
                Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature(el.getSignature());
                bySignature.forEach(phpNamedElementCollections::add);
            });
        }

        return phpNamedElementCollections;
    }
}
