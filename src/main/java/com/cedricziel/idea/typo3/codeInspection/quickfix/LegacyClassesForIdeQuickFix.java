package com.cedricziel.idea.typo3.codeInspection.quickfix;

import com.cedricziel.idea.typo3.index.php.LegacyClassesForIDEIndex;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.inspections.quickfix.PhpQuickFixBase;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class LegacyClassesForIdeQuickFix extends PhpQuickFixBase {
    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Migrate class usage";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

        PsiElement psiElement = descriptor.getPsiElement();
        if (DumbService.isDumb(project)) {
            showIsInDumpModeMessage(project, psiElement);
            return;
        }

        if (psiElement instanceof ClassReference) {
            ClassReference classReference = (ClassReference) psiElement;
            String fqn = classReference.getFQN();
            if (fqn != null) {
                String replacementFQN = LegacyClassesForIDEIndex.findReplacementClass(project, fqn);
                if (replacementFQN != null) {
                    try {
                        classReference.replace(PhpPsiElementFactory.createClassReference(project, replacementFQN));
                    } catch (IncorrectOperationException e) {
                        showErrorMessage(project, "Could not replace class reference", psiElement);
                    }
                }
            }
        }
    }
}
