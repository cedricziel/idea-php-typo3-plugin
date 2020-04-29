package com.cedricziel.idea.typo3.javaScript.codeInspection;

import com.cedricziel.idea.fluid.lang.psi.ArgumentList;
import com.cedricziel.idea.typo3.util.JavaScriptUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public class MissingModulePHPInspection extends PhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpStringLiteralExpression(StringLiteralExpression expression) {
                if (!getLoadRequireJsModulePattern().accepts(expression)) {
                    super.visitPhpStringLiteralExpression(expression);
                    return;
                }

                PsiElement firstParent = PsiTreeUtil.findFirstParent(expression, e -> e instanceof MethodReference);
                if (!(firstParent instanceof MethodReference)) {
                    super.visitPhpStringLiteralExpression(expression);
                    return;
                }

                MethodReference methodReference = (MethodReference) firstParent;
                if (methodReference.getName() == null || !methodReference.getName().equals("loadRequireJsModule")) {
                    super.visitPhpStringLiteralExpression(expression);
                    return;
                }

                if (expression.getPrevPsiSibling() instanceof StringLiteralExpression) {
                    super.visitPhpStringLiteralExpression(expression);
                    return;
                }

                if (JavaScriptUtil.getModuleMap(expression.getProject()).containsKey(expression.getContents())) {
                    return;
                }

                problemsHolder.registerProblem(expression, String.format("Unknown JavaScript module \"%s\"", expression.getContents()));
            }
        };
    }

    @NotNull
    private PsiElementPattern.Capture<PsiElement> getLoadRequireJsModulePattern() {
        return PlatformPatterns.psiElement().withSuperParent(
            2,
            PlatformPatterns.psiElement(MethodReference.class)
        );
    }
}
