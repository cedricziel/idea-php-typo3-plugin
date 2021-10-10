package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.quickfix.LegacyClassesForIdeQuickFix;
import com.cedricziel.idea.typo3.index.php.LegacyClassesForIDEIndex;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class LegacyClassesForIDEInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
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
