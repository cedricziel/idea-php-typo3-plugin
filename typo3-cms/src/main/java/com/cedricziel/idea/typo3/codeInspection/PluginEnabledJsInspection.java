package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public abstract class PluginEnabledJsInspection extends JSInspection {
    @NotNull
    @Override
    protected PsiElementVisitor createVisitor(@NotNull ProblemsHolder problemsHolder, @NotNull LocalInspectionToolSession localInspectionToolSession) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new JSElementVisitor() {
            };
        }

        return buildRealVisitor(problemsHolder, localInspectionToolSession);
    }

    @NotNull
    public abstract PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, @NotNull LocalInspectionToolSession localInspectionToolSession);
}
