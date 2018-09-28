package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.codeInspection.quickfix.LegacyClassesForIdeQuickFix;
import com.cedricziel.idea.typo3.index.php.LegacyClassesForIDEIndex;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class LegacyClassesForIDEInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Legacy class used";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    @Override
    public String getShortName() {
        return "LegacyClassesForIDEInspection";
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
            public void visitPhpClassReference(ClassReference classReference) {
                if (classReference.getFQN() != null && LegacyClassesForIDEIndex.isLegacyClass(classReference.getProject(), classReference.getFQN())) {
                    problemsHolder.registerProblem(classReference, "Legacy class usage", ProblemHighlightType.LIKE_DEPRECATED, new LegacyClassesForIdeQuickFix());
                }

                super.visitPhpClassReference(classReference);
            }

            @Override
            public void visitPhpClassConstantReference(ClassConstantReference constantReference) {
                super.visitPhpClassConstantReference(constantReference);
            }
        };
    }
}
