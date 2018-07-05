package com.cedricziel.idea.typo3.extbase.persistence.codeInsight;

import com.cedricziel.idea.typo3.extbase.ExtbasePatterns;
import com.cedricziel.idea.typo3.extbase.ExtbaseUtils;
import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class QueryCompletionContributor extends CompletionContributor {
    public QueryCompletionContributor() {
        this.extend(CompletionType.BASIC, ExtbasePatterns.stringArgumentOnMethodCallPattern(), new QueryCompletionProvider());
    }

    public static class QueryCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement element = parameters.getOriginalPosition();

            Method containingMethod = (Method) PsiTreeUtil.findFirstParent(element, x -> PlatformPatterns.psiElement(Method.class).accepts(x));
            MethodReference methodReference = (MethodReference) PsiTreeUtil.findFirstParent(element, x -> PlatformPatterns.psiElement(MethodReference.class).accepts(x));

            if (containingMethod == null || methodReference == null) {
                return;
            }

            Method method = (Method) methodReference.resolve();
            if (method == null || method.getContainingClass() == null) {
                return;
            }

            if (!Arrays.asList(ExtbaseUtils.EXTBASE_QUERY_BUILDER_METHODS).contains(method.getName())) {
                return;
            }

            PhpClass repositoryClass = containingMethod.getContainingClass();
            if (repositoryClass == null || !ExtbaseUtils.isRepositoryClass(repositoryClass)) {
                return;
            }

            if (!ExtbaseUtils.methodInstanceOf(ExtbaseUtils.EXTBASE_QUERY_INTERFACE_FQN, method)) {
                return;
            }

            String potentialModelClass = ExtbaseUtility.convertRepositoryFQNToEntityFQN(repositoryClass.getFQN());
            Collection<PhpClass> classesByFQN = PhpIndex.getInstance(element.getProject()).getClassesByFQN(potentialModelClass);
            for (PhpClass x: classesByFQN) {
                x.getFields().stream().filter(field -> !Arrays.asList(ExtbaseUtils.NON_QUERYABLE_ENTITY_FIELDS).contains(field.getName())).forEach(field -> result.addElement(new PhpLookupElement(field)));
            }
        }
    }
}
