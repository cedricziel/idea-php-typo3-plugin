package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class MissingColumnTypeInspection extends PluginEnabledPhpInspection {
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
                if (arrayIndex != null && arrayIndex.equals("type")) {
                    if (element instanceof StringLiteralExpression) {
                        String tableName = ((StringLiteralExpression) element).getContents();
                        boolean isValidRenderType = TCAUtil.getAvailableColumnTypes(element.getProject()).contains(tableName);

                        if (!isValidRenderType) {
                            problemsHolder.registerProblem(element, "Missing column type definition");
                        }
                    }
                }
            }
        };
    }
}
