package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public abstract class PluginEnabledPhpInspection extends PhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PhpElementVisitor() {
            };
        }

        return buildRealVisitor(problemsHolder, b);
    }

    @NotNull
    public abstract PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b);
}
