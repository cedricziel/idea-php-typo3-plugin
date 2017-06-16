package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.container.CoreServiceParser;
import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            String serviceId = ref.getContents();

            return methodReference.getSignature() + "%" + serviceId;
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {

        String serviceName = expression.split("%")[1];

        Collection<PhpNamedElement> phpNamedElementCollections = new ArrayList<>();
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        CoreServiceParser serviceParser = new CoreServiceParser();
        serviceParser.collect(project);

        List<TYPO3ServiceDefinition> resolvedServices = serviceParser.resolve(project, serviceName);
        if (resolvedServices == null || resolvedServices.isEmpty()) {
            return phpNamedElementCollections;
        }

        resolvedServices.forEach(serviceDefinition -> {
            Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN(serviceDefinition.getClassName());
            phpNamedElementCollections.addAll(classesByFQN);
        });

        return phpNamedElementCollections;
    }
}
