package com.cedricziel.idea.typo3.translation.codeInspection;

import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public class TranslationMissingInspection extends PhpInspection {

    public static final String MESSAGE = "Missing translation key";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
                if (expression == null || expression.getParent() instanceof ConcatenationExpression) {
                    return;
                }

                String contents = expression.getContents();
                if (TranslationUtil.isTranslationKeyString(contents) && !TranslationUtil.keyExists(problemsHolder.getProject(), contents)) {
                    // new CreateMissingTranslationQuickFix(contents)
                    problemsHolder.registerProblem(expression, MESSAGE);
                }
            }
        };
    }
}
