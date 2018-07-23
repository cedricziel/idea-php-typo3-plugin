package com.cedricziel.idea.fluid.codeInsight.inspection;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidBoundNamespace;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class UndefinedNamespaceInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!(element instanceof FluidBoundNamespace)) {
                    super.visitElement(element);

                    return;
                }

                for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
                    Collection<FluidNamespace> fluidNamespaces = extension.provideForElement(element);
                    for (FluidNamespace fluidNamespace : fluidNamespaces) {
                        if (fluidNamespace.prefix.equals(element.getText())) {
                            super.visitElement(element);

                            return;
                        }
                    }
                }

                holder.registerProblem(element, "Unbound  viewHelper namespace", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);

                super.visitElement(element);
            }
        };
    }


}
