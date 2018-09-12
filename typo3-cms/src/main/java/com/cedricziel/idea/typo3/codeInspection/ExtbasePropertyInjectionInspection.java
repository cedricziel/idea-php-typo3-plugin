package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.codeInspection.quickfix.CreateInjectorQuickFix;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ExtbasePropertyInjectionInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.PERFORMANCE_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "use method injection instead of field injection";
    }

    @NotNull
    public String getShortName() {
        return "ExtbasePropertyInjection";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PhpElementVisitor() {
            };
        }

        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {
                if (element instanceof PhpDocTag) {
                    PhpDocTag tag = (PhpDocTag) element;
                    if ("@inject".equals(tag.getName())) {
                        problemsHolder.registerProblem(element, "Extbase property injection", new CreateInjectorQuickFix(element));
                    }
                }
            }
        };
    }
}
