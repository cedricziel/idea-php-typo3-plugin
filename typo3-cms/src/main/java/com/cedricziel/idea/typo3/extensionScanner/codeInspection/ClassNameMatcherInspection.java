package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.cedricziel.idea.typo3.util.DeprecationUtility.getDeprecatedClassNames;

public class ClassNameMatcherInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {

            @Override
            public void visitPhpClassReference(ClassReference classReference) {
                Set<String> deprecatedClassNames = getDeprecatedClassNames(classReference.getProject());
                if (deprecatedClassNames.contains(classReference.getFQN())) {
                    problemsHolder.registerProblem(classReference, "Class scheduled for removal in upcoming TYPO3 version, consider using an alternative");
                }

                super.visitPhpClassReference(classReference);
            }
        };
    }
}
