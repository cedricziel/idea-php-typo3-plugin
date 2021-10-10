package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.quickfix.CreateInjectorQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public class ExtbasePropertyInjectionInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpDocTag(PhpDocTag tag) {
                if ("@inject".equals(tag.getName())) {
                    problemsHolder.registerProblem(tag, "Extbase property injection", new CreateInjectorQuickFix(tag));
                }

                super.visitPhpDocTag(tag);
            }
        };
    }
}
