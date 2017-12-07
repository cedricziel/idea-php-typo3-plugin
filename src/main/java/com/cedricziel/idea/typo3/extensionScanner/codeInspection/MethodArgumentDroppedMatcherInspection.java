package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.util.ExtensionScannerUtil;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MethodArgumentDroppedMatcherInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Number of arguments changed with TYPO3 9";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                if (!PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(element)) {
                    return;
                }

                MethodReference methodReference = (MethodReference) element;
                ParameterList parameterList = methodReference.getParameterList();
                PhpExpression classReference = methodReference.getClassReference();
                if (classReference != null) {
                    PhpType inferredType = classReference.getInferredType();
                    String compiledClassMethodKey = inferredType.toString() + "->" + methodReference.getName();
                    if (ExtensionScannerUtil.classMethodHasDroppedArguments(element.getProject(), compiledClassMethodKey)) {
                        Integer maximumNumberOfArguments = ExtensionScannerUtil.getMaximumNumberOfArguments(element.getProject(), compiledClassMethodKey);

                        if (parameterList != null && maximumNumberOfArguments != -1 && parameterList.getParameters().length != maximumNumberOfArguments) {
                            problemsHolder.registerProblem(element, "Number of arguments changed with TYPO3 9, consider refactoring");
                        }
                    }
                }
            }
        };
    }
}
