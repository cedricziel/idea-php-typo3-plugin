package com.cedricziel.idea.fluid.codeInsight.inspection;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class UndefinedViewHelperInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!(element instanceof FluidViewHelperReference)) {
                    super.visitElement(element);

                    return;
                }

                for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
                    Collection<FluidNamespace> fluidNamespaces = extension.provideForElement(element);
                    for (ViewHelperProvider viewHelperProvider : ViewHelperProvider.EP_NAME.getExtensions()) {
                        for (FluidNamespace fluidNamespace : fluidNamespaces) {
                            Map<String, ViewHelper> stringViewHelperMap = viewHelperProvider.provideForNamespace(element.getProject(), fluidNamespace.namespace);

                            if (stringViewHelperMap.containsKey(element.getText())) {
                                super.visitElement(element);

                                return;
                            }
                        }
                    }
                }

                holder.registerProblem(element, "Undefined viewHelper", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);

                super.visitElement(element);
            }
        };
    }


}
