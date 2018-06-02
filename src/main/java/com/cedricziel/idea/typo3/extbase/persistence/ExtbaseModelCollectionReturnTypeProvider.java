package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
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

        if (!(psiElement instanceof Field)) {
            return null;
        }

        PhpClass containingClass = ((Field) psiElement).getContainingClass();
        if (containingClass == null) {
            return null;
        }

        Collection<PhpClass> classesByFQN = PhpIndex.getInstance(psiElement.getProject()).getClassesByFQN(TYPO3_CMS_EXTBASE_DOMAIN_OBJECT_ABSTRACT_ENTITY);
        if (classesByFQN.isEmpty()) {
            return null;
        }

        PhpClass abstractEntityClass = classesByFQN.iterator().next();

        if (!PhpClassHierarchyUtils.isSuperClass(abstractEntityClass, containingClass, true)) {
            return null;
        }

        PhpDocComment docComment = ((Field) psiElement).getDocComment();
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

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return PhpIndex.getInstance(project).getBySignature(expression);
    }
}
