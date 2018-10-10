package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class DeprecatedIconUsageInspection extends PhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PsiElementVisitor() {
            };
        }

        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!stringParameterInMethodReference().accepts(element)) {
                    return;
                }

                StringLiteralExpression literalExpression = (StringLiteralExpression) element;
                String value = literalExpression.getContents();

                if (value.isEmpty()) {
                    return;
                }

                PsiElement methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
                if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getIcon")) {
                    if (IconIndex.getDeprecatedIconIdentifiers(problemsHolder.getProject()).containsKey(value)) {
                        problemsHolder.registerProblem(element, "Deprecated icon usage");
                    }
                }

                super.visitElement(element);
            }
        };
    }

    private PsiElementPattern.Capture<StringLiteralExpression> stringParameterInMethodReference() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(ParameterList.class);
    }
}
