package com.cedricziel.idea.typo3.extbase;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

import static com.cedricziel.idea.typo3.extbase.persistence.ExtbasePersistenceCompletionContributor.ExtbaseRepositoryMagicMethodsCompletionProvider.TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY;

public class ExtbaseUtils {
    public static final String TYPO3_CMS_EXTBASE_DOMAIN_OBJECT_ABSTRACT_ENTITY = "TYPO3\\CMS\\Extbase\\DomainObject\\AbstractEntity";

    @Nullable
    public static PhpClass getBaseRepositoryClass(@NotNull Project project) {
        Iterator<PhpClass> iterator = PhpIndex.getInstance(project).getClassesByFQN(TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY).iterator();
        if (!iterator.hasNext()) {
            return null;
        }

        PhpClass repositoryClass = iterator.next();
        if (repositoryClass == null) {
            return null;
        }

        return repositoryClass;
    }

    public static boolean isRepositoryClass(@NotNull PhpClass phpClass) {
        PhpClass baseRepositoryClass = getBaseRepositoryClass(phpClass.getProject());
        if (baseRepositoryClass == null) {
            return false;
        }

        return PhpClassHierarchyUtils.isSuperClass(baseRepositoryClass, phpClass, true);
    }

    private boolean isEntityClass(PsiElement psiElement) {
        PhpClass containingClass = ((PhpClassMember) psiElement).getContainingClass();
        if (containingClass == null) {
            return false;
        }

        Collection<PhpClass> classesByFQN = PhpIndex.getInstance(psiElement.getProject()).getClassesByFQN(TYPO3_CMS_EXTBASE_DOMAIN_OBJECT_ABSTRACT_ENTITY);
        if (classesByFQN.isEmpty()) {
            return false;
        }

        PhpClass abstractEntityClass = classesByFQN.iterator().next();
        return PhpClassHierarchyUtils.isSuperClass(abstractEntityClass, containingClass, true);
    }
}
