package com.cedricziel.idea.typo3.extbase.persistence;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.PhpReferenceResolver;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cedricziel.idea.typo3.extbase.persistence.ExtbasePersistenceCompletionContributor.ExtbaseRepositoryMagicMethodsCompletionProvider.TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY;

public class ExtbasePersistenceReferenceResolver implements PhpReferenceResolver {
    @Override
    public Collection<? extends PhpNamedElement> resolve(PhpReference phpReference) {
        List<PhpNamedElement> elements = new ArrayList<>();

        if (!(phpReference instanceof MethodReference)) {
            return Collections.emptyList();
        }

        String methodName = phpReference.getName();
        if (methodName == null || (!methodName.startsWith("countBy") && !methodName.startsWith("findOneBy") && !methodName.startsWith("findBy"))) {
            return Collections.emptyList();
        }

        PhpTypedElement variable = PsiTreeUtil.findChildOfType(phpReference, PhpTypedElement.class);
        if (variable == null) {
            return Collections.emptyList();
        }

        Collection<PhpClass> classesByFQN = new ArrayList<>();

        for (String s: variable.getType().getTypes()) {
            classesByFQN.addAll(PhpIndex.getInstance(phpReference.getProject()).getClassesByFQN(s));
        }

        if (classesByFQN.isEmpty()) {
            return Collections.emptyList();
        }

        classesByFQN.forEach(repositoryClass -> {
            if (!PhpElementsUtil.hasSuperClass(repositoryClass, TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY)) {
                return;
            }

            String entityFQN = ExtbaseUtility.convertRepositoryFQNToEntityFQN(repositoryClass.getFQN());
            Collection<PhpClass> entityClasses = PhpIndex.getInstance(phpReference.getProject()).getClassesByFQN(entityFQN);
            if (entityClasses.isEmpty()) {
                return;
            }

            String fieldName;
            if (methodName.startsWith("countBy")) {
                fieldName = StringUtils.uncapitalize(methodName.substring("countBy".length()));
            } else if (methodName.startsWith("findOneBy")) {
                fieldName = StringUtils.uncapitalize(methodName.substring("findOneBy".length()));
            } else if (methodName.startsWith("findBy")) {
                fieldName = StringUtils.uncapitalize(methodName.substring("findBy".length()));
            } else {
                return;
            }

            entityClasses.forEach(c -> {
                // field
                Field fieldByName = c.findFieldByName(fieldName, false);
                if (fieldByName != null) {
                    elements.add(fieldByName);
                }

                // __call method
                Method callMethod = repositoryClass.findMethodByName("__call");
                if (callMethod != null) {
                    elements.add(callMethod);
                }
            });
        });

        return elements;
    }
}
