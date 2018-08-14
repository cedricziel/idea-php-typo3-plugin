package com.cedricziel.idea.typo3.translation.codeInspection;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class TranslationMissingInspection extends LocalInspectionTool {

    public static final String MESSAGE = "Missing translation key";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof StringLiteralExpression) {
                    visitPhpStringLiteralExpression((StringLiteralExpression) element);
                }

                super.visitElement(element);
            }

            private void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
                if (expression == null || expression.getParent() instanceof ConcatenationExpression) {
                    return;
                }

                String contents = expression.getContents();
                if (TranslationUtil.isTranslationKeyString(contents) && contents.length() > 4 && !TranslationUtil.keyExists(problemsHolder.getProject(), contents)) {
                    if (ResourcePathIndex.projectContainsResourceFile(expression.getProject(), contents)) {
                        // string references a translation file instead
                        return;
                    }

                    // may be some form of concatenation
                    if (contents.endsWith(":")) {
                        return;
                    }

                    // new CreateMissingTranslationQuickFix(contents)
                    problemsHolder.registerProblem(expression, MESSAGE, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }
        };
    }
}
