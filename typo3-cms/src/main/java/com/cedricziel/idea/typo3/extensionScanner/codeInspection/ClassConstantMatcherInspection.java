package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.cedricziel.idea.typo3.util.DeprecationUtility;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ClassConstantMatcherInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {

            @Override
            public void visitPhpClassConstantReference(ClassConstantReference constantReference) {
                if (DeprecationUtility.isDeprecated(problemsHolder.getProject(), constantReference)) {
                    problemsHolder.registerProblem(constantReference, "Deprecated class constant");
                }

                super.visitPhpClassConstantReference(constantReference);
            }
        };
    }

}
