package com.cedricziel.idea.typo3.extbase.persistence;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.extbase.ExtbaseUtils;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.PhpReferenceResolver2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class ExtbasePersistenceReferenceResolver implements PhpReferenceResolver2 {
    @Override
    public Collection<? extends PhpNamedElement> resolve(PhpReference phpReference, Collection<? extends PhpNamedElement> candidates) {
        if (!TYPO3CMSProjectSettings.isEnabled(phpReference)) {
            return candidates;
        }

        if (!(phpReference instanceof MethodReference)) {
            return candidates;
        }

        String methodName = phpReference.getName();
        if (methodName == null || (!methodName.startsWith("countBy") && !methodName.startsWith("findOneBy") && !methodName.startsWith("findBy"))) {
            return candidates;
        }

        PhpTypedElement variable = PsiTreeUtil.findChildOfType(phpReference, PhpTypedElement.class);
        if (variable == null) {
            return candidates;
        }

        Collection<PhpNamedElement> elements = new ArrayList<>();
        Collection<PhpClass> classesByFQN = new ArrayList<>();

        for (String s: variable.getType().getTypes()) {
            classesByFQN.addAll(PhpIndex.getInstance(phpReference.getProject()).getClassesByFQN(s));
        }

        if (classesByFQN.isEmpty()) {
            return candidates;
        }

        classesByFQN.forEach(repositoryClass -> {
            if (!PhpElementsUtil.hasSuperClass(repositoryClass, ExtbaseUtils.TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY)) {
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

        return (Collection<? extends PhpNamedElement>) CollectionUtils.union(candidates, elements);
    }
}
