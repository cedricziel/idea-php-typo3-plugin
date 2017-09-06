package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

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
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (!(psiElement instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(psiElement, "get")) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        if (methodReference.getParameters().length == 0) {
            return null;
        }

        PsiElement firstParam = methodReference.getParameters()[0];
        if (firstParam instanceof PhpReference) {
            PhpReference ref = (PhpReference) firstParam;
            if (ref.getText().toLowerCase().contains("::class")) {
                return new PhpType().add("#" + this.getKey() + ref.getSignature());
            }
        }

        return null;
    }

    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        if(expression.startsWith("#K#C")) {
            // PhpStorm9: #K#C\Class\Foo.class
            if(expression.endsWith(".class")) {
                expression = StringUtils.stripStart(expression.substring(4, expression.length() - 6), "\\");
            }
        }

        return PhpIndex.getInstance(project).getClassesByFQN(expression);
    }
}
