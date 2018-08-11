package com.cedricziel.idea.typo3.dispatcher;

import com.cedricziel.idea.typo3.TYPO3Patterns;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SignalDispatcherReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {

        registrar.registerReferenceProvider(
            TYPO3Patterns.connectSignalMethodNameString(),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    StringLiteralExpression subject = (StringLiteralExpression) element;
                    ParameterList parameterList = (ParameterList) PsiTreeUtil.findFirstParent(subject, e -> e instanceof ParameterList);
                    if (parameterList == null) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    List<PsiElement> parameters = Arrays.asList(parameterList.getParameters());
                    if (parameters.indexOf(subject) == 3) {
                        PsiElement className = parameters.get(2);
                        if (className instanceof ClassConstantReference) {
                            if (subject.getContents().length() > subject.getText().length()) {
                                return new SignalSlotMethodReference[]{
                                    new SignalSlotMethodReference((ClassConstantReference) className, subject, new TextRange(1, subject.getContents().length()))
                                };
                            }

                            return new SignalSlotMethodReference[]{
                                new SignalSlotMethodReference((ClassConstantReference) className, subject)
                            };
                        }
                        if (className instanceof StringLiteralExpression) {
                            if (subject.getContents().length() > subject.getText().length()) {
                                return new SignalSlotMethodReference[]{
                                    new SignalSlotMethodReference((StringLiteralExpression) className, subject, new TextRange(1, subject.getContents().length()))
                                };
                            }

                            return new SignalSlotMethodReference[]{
                                new SignalSlotMethodReference((StringLiteralExpression) className, subject)
                            };
                        }
                    }

                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );
    }
}
