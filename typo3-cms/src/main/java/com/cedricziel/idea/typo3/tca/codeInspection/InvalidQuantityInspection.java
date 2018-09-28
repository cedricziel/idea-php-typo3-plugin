package com.cedricziel.idea.typo3.tca.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class InvalidQuantityInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Config key only accepts integer values";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.isEnabled(problemsHolder.getProject())) {
            return new PhpElementVisitor() {
            };
        }

        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                boolean isArrayStringValue = PhpElementsUtil.isStringArrayValue().accepts(element);
                if (!isArrayStringValue || !insideTCAColumnDefinition(element)) {
                    return;
                }

                String arrayIndex = extractArrayIndexFromValue(element);
                if (arrayIndex != null && Arrays.asList(TCAUtil.TCA_NUMERIC_CONFIG_KEYS).contains(arrayIndex)) {
                    if (element instanceof StringLiteralExpression) {
                        problemsHolder.registerProblem(element, "Config key only accepts integer values");
                    }
                }
            }
        };
    }
}
