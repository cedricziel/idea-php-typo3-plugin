package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtbaseModelCollectionReturnTypeProvider implements PhpTypeProvider4 {
    @Nullable
    private static PhpType inferTypeFromClassMember(PhpClassMember classMember) {
        if (classMember == null) {
            return null;
        }

        PhpDocComment docComment = classMember.getDocComment();
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
    public char getKey() {
        return '\u0278';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (psiElement instanceof FieldReference) {
            String signature = StringUtils.strip(((FieldReference) psiElement).getSignature(), "\\");
            return new PhpType().add("#" + this.getKey() + signature);
        }

        if (psiElement instanceof MethodReference && ((MethodReference) psiElement).getName() != null && ((MethodReference) psiElement).getName().startsWith("get")) {
            String signature = StringUtils.strip(((MethodReference) psiElement).getSignature(), "\\");
            return new PhpType().add("#" + this.getKey() + signature);
        }

        return null;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    @Nullable
    private Field extractFieldFromGetter(MethodReference methodReference) {
        String name = methodReference.getName();
        if (name == null) {
            return null;
        }

        String substring = name.substring(2);
        char[] cArr = substring.toCharArray();
        if (cArr.length == 0) {
            return null;
        }

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

    @Nullable
    private Field extractFieldFromGetter(Method method) {
        String substring = method.getName().substring(3);
        char[] cArr = substring.toCharArray();
        if (cArr.length == 0) {
            return null;
        }

        cArr[0] = Character.toLowerCase(cArr[0]);

        String propertyName = new String(cArr);

        PhpClass containingClass = method.getContainingClass();

        if (containingClass == null) {
            return null;
        }

        return containingClass.findFieldByName(propertyName, false);
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        Collection<? extends PhpNamedElement> bySignature = PhpIndex.getInstance(project).getBySignature(StringUtils.strip(expression, "\\"));

        List<PhpNamedElement> phpNamedElements = new ArrayList<>();
        for (PhpNamedElement phpNamedElement: bySignature) {
            if (phpNamedElement instanceof Field) {
                PhpType type = inferTypeFromClassMember((Field) phpNamedElement);
                phpNamedElement.getType().add(type);

                phpNamedElements.add(phpNamedElement);
            } else if (phpNamedElement instanceof Method) {
                phpNamedElement.getType().add(inferTypeFromClassMember((Method) phpNamedElement));
                phpNamedElement.getType().add(inferTypeFromClassMember(extractFieldFromGetter((Method) phpNamedElement)));

                MethodReturnTypeVisitor visitor = new MethodReturnTypeVisitor();
                visitor.visitElement(phpNamedElement);

                phpNamedElement.getType().add(visitor.getType());

                phpNamedElements.add(phpNamedElement);
            } else if (phpNamedElement instanceof MethodReference) {
                phpNamedElement.getType().add(inferTypeFromClassMember(extractFieldFromGetter((MethodReference) phpNamedElement)));

                phpNamedElements.add(phpNamedElement);
            }
        }

        return phpNamedElements;
    }

    private static class MethodReturnTypeVisitor extends PsiRecursiveElementVisitor {
        private final PhpType type;

        public MethodReturnTypeVisitor() {
            super();

            this.type = new PhpType();
        }

        @Override
        public void visitElement(PsiElement element) {
            super.visitElement(element);

            if (PlatformPatterns.psiElement(FieldReference.class).withParent(PhpReturn.class).accepts(element)) {
                Field f = (Field) ((FieldReference) element).resolve();
                if (f == null) {
                    return;
                }

                this.type.add(inferTypeFromClassMember(f));
            }
        }

        public PhpType getType() {
            return type;
        }
    }
}
