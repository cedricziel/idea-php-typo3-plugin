package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.cedricziel.idea.typo3.util.ExtensionScannerUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

public class MethodArgumentDroppedMatcherInspection extends PluginEnabledPhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpMethodReference(MethodReference reference) {
                ParameterList parameterList = reference.getParameterList();
                PhpExpression classReference = reference.getClassReference();
                if (classReference != null) {
                    PhpType inferredType = classReference.getInferredType();
                    String compiledClassMethodKey = inferredType.toString() + "->" + reference.getName();
                    if (ExtensionScannerUtil.classMethodHasDroppedArguments(reference.getProject(), compiledClassMethodKey)) {
                        int maximumNumberOfArguments = ExtensionScannerUtil.getMaximumNumberOfArguments(reference.getProject(), compiledClassMethodKey);

                        if (parameterList != null && maximumNumberOfArguments != -1 && parameterList.getParameters().length != maximumNumberOfArguments) {
                            problemsHolder.registerProblem(reference, "Number of arguments changes with upcoming TYPO3 version, consider refactoring");
                        }
                    }
                }

                super.visitPhpMethodReference(reference);
            }
        };
    }
}
