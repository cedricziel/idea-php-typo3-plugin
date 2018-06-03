package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtbaseModelCollectionReturnTypeProvider implements PhpTypeProvider3 {

    public static final String TYPO3_CMS_EXTBASE_DOMAIN_OBJECT_ABSTRACT_ENTITY = "TYPO3\\CMS\\Extbase\\DomainObject\\AbstractEntity";

    @Override
    public char getKey() {
        return '\u0278';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (!(psiElement instanceof Field) && !isGetter(psiElement)) {
            return null;
        }

        if (!isEntityClass(psiElement)) {
            return null;
        }

        return extractReturnType(psiElement);
    }

    private PhpType extractReturnType(PsiElement psiElement) {
        Field field;
        if (psiElement instanceof MethodReference) {
            field = extractFieldFromGetter((MethodReference) psiElement);
        } else if (psiElement instanceof Method) {
            field = extractFieldFromGetter((Method) psiElement);
        } else {
            field = ((Field) psiElement);
        }

        if (field == null) {
            return null;
        }

        PhpDocComment docComment = field.getDocComment();
        if (docComment == null) {
            return null;
        }

        PhpDocParamTag varTag = docComment.getVarTag();
        if (varTag == null) {
            return null;
        }

        String text = varTag.getText();
        if (!text.contains("ObjectStorage<")) {
            return null;
        }

        PhpType phpType = new PhpType();

        String pattern = "<(.*?)>";
        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher(text);
        while (matcher.find()) {
            phpType.add(matcher.group(1) + "[]");
        }

        return phpType;
    }

    private Field extractFieldFromGetter(MethodReference methodReference) {
        String name = methodReference.getName();
        if (name == null) {
            return null;
        }

        String substring = name.substring(2);
        char[] cArr = substring.toCharArray();
        cArr[0] = Character.toLowerCase(cArr[0]);

        String propertyName = new String(cArr);

        PsiElement method = methodReference.resolve();
        if (!(method instanceof Method)) {
            return null;
        }

        PhpClass containingClass = ((Method) method).getContainingClass();

        if (containingClass == null) {
            return null;
        }

        return containingClass.findFieldByName(propertyName, true);
    }

    private Field extractFieldFromGetter(Method method) {
        String substring = method.getName().substring(3);
        char[] cArr = substring.toCharArray();
        cArr[0] = Character.toLowerCase(cArr[0]);

        String propertyName = new String(cArr);

        PhpClass containingClass = method.getContainingClass();

        if (containingClass == null) {
            return null;
        }

        return containingClass.findFieldByName(propertyName, false);
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

    private boolean isGetter(PsiElement psiElement) {
        return (psiElement instanceof Method) && ((Method) psiElement).getName().startsWith("get");
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return PhpIndex.getInstance(project).getBySignature(expression);
    }
}
