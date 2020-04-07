package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TYPO3Utility;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ContextTypeProvider implements PhpTypeProvider4 {
    @Override
    public char getKey() {
        return '\u2636';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb() || !TYPO3CMSProjectSettings.isEnabled(psiElement)) {
            return null;
        }

        if (!(psiElement instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(psiElement, "getAspect")) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        if (methodReference.getParameters().length == 0) {
            return null;
        }

        PsiElement firstParam = methodReference.getParameters()[0];
        if (firstParam instanceof StringLiteralExpression) {
            StringLiteralExpression contextAlias = (StringLiteralExpression) firstParam;
            if (!contextAlias.getContents().isEmpty()) {
                return new PhpType().add("#" + this.getKey() + contextAlias.getContents());
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Set<String> set, int i, Project project) {
        List<PhpClass> phpClasses = new ArrayList<>();
        for (String s1 : set) {
            String[] split = s1.split(String.valueOf(getKey()));
            if (split.length == 2) {
                String fqn = TYPO3Utility.getFQNByAspectName(split[1]);
                if (fqn != null) {
                    phpClasses.addAll(PhpIndex.getInstance(project).getAnyByFQN(fqn));
                }
            }
        }

        return phpClasses;
    }
}
