package com.cedricziel.idea.typo3.extbase;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ExtbaseUtils {
    public static final String EXTBASE_ABSTRACT_ENTITY_FQN = "TYPO3\\CMS\\Extbase\\DomainObject\\AbstractEntity";

    public static String EXTBASE_QUERY_INTERFACE_FQN = "TYPO3\\CMS\\Extbase\\Persistence\\QueryInterface";

    public static String[] EXTBASE_QUERY_BUILDER_METHODS = {
            "equals",
            "like",
            "contains",
            "in",
            "lessThan",
            "lessThanOrEqual",
            "greaterThan",
            "greaterThanOrEqual",
            "isEmpty",
    };

    public static final String OBJECT_STORAGE_FQN = "TYPO3\\CMS\\Extbase\\Persistence\\ObjectStorage";

    public static final String TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY = "TYPO3\\CMS\\Extbase\\Persistence\\Repository";

    public static final String QUERY_RESULT_INTERFACE = "TYPO3\\CMS\\Extbase\\Persistence\\QueryResultInterface";

    public static final String[] NON_QUERYABLE_ENTITY_FIELDS = {
            "_isClone",
            "_cleanProperties",
    };

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

    public static boolean isObjectStorage(@NotNull Field field) {
        return field.getDeclaredType().toString().contains(OBJECT_STORAGE_FQN);
    }

    public static boolean isNonQueryableField(@NotNull String name) {
        return Arrays.asList(ExtbaseUtils.NON_QUERYABLE_ENTITY_FIELDS).contains(name);
    }

    public static boolean fieldHasMagicFinders(@NotNull Field field) {
        return !field.isConstant() && !field.isInternal() && !ExtbaseUtils.isNonQueryableField(field.getName()) && !ExtbaseUtils.isObjectStorage(field);
    }

    public static boolean methodInstanceOf(@NotNull String className, @NotNull Method method) {
        PhpClass containingClass = method.getContainingClass();

        return containingClass != null && containingClass.getPresentableFQN().equals(className);
    }

    private boolean isEntityClass(PsiElement psiElement) {
        PhpClass containingClass = ((PhpClassMember) psiElement).getContainingClass();
        if (containingClass == null) {
            return false;
        }

        Collection<PhpClass> classesByFQN = PhpIndex.getInstance(psiElement.getProject()).getClassesByFQN(EXTBASE_ABSTRACT_ENTITY_FQN);
        if (classesByFQN.isEmpty()) {
            return false;
        }

        PhpClass abstractEntityClass = classesByFQN.iterator().next();
        return PhpClassHierarchyUtils.isSuperClass(abstractEntityClass, containingClass, true);
    }
}
