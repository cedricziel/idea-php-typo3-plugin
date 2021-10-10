package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class MissingRenderTypeInspection extends PluginEnabledPhpInspection {
    @NotNull
    public String getDisplayName() {
        return "Missing renderType definition";
    }

    @NotNull
    public String getShortName() {
        return "MissingRenderTypeInspection";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                boolean isArrayStringValue = PhpElementsUtil.isStringArrayValue().accepts(element);
                if (!isArrayStringValue || !insideTCAColumnDefinition(element)) {
                    return;
                }


                String arrayIndex = extractArrayIndexFromValue(element);
                if (arrayIndex != null && arrayIndex.equals("renderType")) {
                    if (element instanceof StringLiteralExpression) {
                        String tableName = ((StringLiteralExpression) element).getContents();
                        boolean isValidRenderType = TCAUtil.getAvailableRenderTypes(element).contains(tableName);

                        if (!isValidRenderType) {
                            problemsHolder.registerProblem(element, "Missing renderType definition");
                        }
                    }
                }
            }
        };
    }
}
