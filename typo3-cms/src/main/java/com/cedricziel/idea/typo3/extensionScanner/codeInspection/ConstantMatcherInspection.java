package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.cedricziel.idea.typo3.util.DeprecationUtility;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ConstantReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public class ConstantMatcherInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpConstantReference(ConstantReference reference) {
                if (DeprecationUtility.isDeprecated(problemsHolder.getProject(), reference)) {
                    problemsHolder.registerProblem(reference, "Constant scheduled for removal in upcoming TYPO3 version, consider using an alternative");
                }

                super.visitPhpConstantReference(reference);
            }
        };
    }
}
