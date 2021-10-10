package com.cedricziel.idea.typo3.translation.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class TranslationMissingInspection extends PluginEnabledPhpInspection {

    public static final String MESSAGE = "Missing translation key";

    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            public void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
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
                    problemsHolder.registerProblem(expression, MESSAGE, ProblemHighlightType.WEAK_WARNING);
                }
            }
        };
    }
}
