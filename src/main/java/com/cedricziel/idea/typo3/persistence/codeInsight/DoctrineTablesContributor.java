package com.cedricziel.idea.typo3.persistence.codeInsight;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Matches
 * <p>
 * \TYPO3\CMS\Core\Database::getConnectionForTable()
 * \TYPO3\CMS\Core\Database::getQueryBuilderForTable()
 * <p>
 * and provides autocompletion
 */
public class DoctrineTablesContributor extends CompletionContributor {

    public DoctrineTablesContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                PsiElement element = completionParameters.getPosition().getParent();
                ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
                if (parameterList == null) {
                    return;
                }

                MethodReference methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
                if (methodReference == null) {
                    return;
                }

                if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getConnectionForTable")) {
                    TableUtil.completeAvailableTableNames(methodReference.getProject(), completionResultSet);

                    return;
                }

                if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getQueryBuilderForTable")) {
                    TableUtil.completeAvailableTableNames(methodReference.getProject(), completionResultSet);

                    return;
                }

                String methodName = methodReference.getName();
                ClassReference classReference = PsiTreeUtil.getChildOfType(methodReference, ClassReference.class);
                if (classReference != null && methodName != null) {
                    String name = classReference.getFQN();

                    // there can be multiple classes in one project scope that share the same FQN
                    Collection<PhpClass> phpClasses = PhpIndex.getInstance(element.getProject()).getClassesByFQN(name);
                    for (PhpClass c : phpClasses) {
                        Method method = c.findMethodByName(methodName);

                        ParameterList originalMethodParameterList = PsiTreeUtil.getChildOfType(method, ParameterList.class);
                        if (originalMethodParameterList != null) {
                            Parameter firstParameter = PsiTreeUtil.getChildOfType(originalMethodParameterList, Parameter.class);

                            // completion on method with no arguments
                            if (firstParameter == null) {
                                return;
                            }

                            String parameterName = firstParameter.getName();
                            if (parameterName.equals("table") || parameterName.equals("tableName")) {
                                TableUtil.completeAvailableTableNames(methodReference.getProject(), completionResultSet);

                                return;
                            }
                        }
                    }
                }
            }
        });
    }
}
