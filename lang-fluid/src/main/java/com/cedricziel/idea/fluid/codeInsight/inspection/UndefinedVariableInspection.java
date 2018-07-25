package com.cedricziel.idea.fluid.codeInsight.inspection;

import com.cedricziel.idea.fluid.lang.psi.FluidFieldChain;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class UndefinedVariableInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!(element instanceof FluidFieldChain)) {
                    super.visitElement(element);
                    return;
                }

                if (!FluidUtil.findVariablesInCurrentContext(element).containsKey(element.getText())) {
                    holder.registerProblem(element, "Undefined variable", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }

                super.visitElement(element);
            }
        };
    }


}
