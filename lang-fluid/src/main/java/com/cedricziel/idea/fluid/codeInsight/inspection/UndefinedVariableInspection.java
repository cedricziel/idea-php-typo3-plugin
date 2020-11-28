package com.cedricziel.idea.fluid.codeInsight.inspection;

import com.cedricziel.idea.fluid.lang.psi.FluidFieldChain;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldExpr;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class UndefinedVariableInspection extends LocalInspectionTool {

    public static final String MESSAGE = "Undefined variable %s";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (!PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(
                    PlatformPatterns.psiElement(FluidFieldChain.class).withParent(FluidFieldExpr.class))
                    .accepts(element)) {
                    super.visitElement(element);

                    return;
                }

                if (!FluidUtil.findVariablesInCurrentContext(element).containsKey(element.getText())) {
                    holder.registerProblem(element, String.format(MESSAGE, element.getText()), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }

                super.visitElement(element);
            }
        };
    }


}
