package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.arrayIndexIsTCATableNameField;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class MissingTableInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Missing table definition";
    }

    @NotNull
    public String getShortName() {
        return "MissingTableInspection";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PhpElementVisitor() {
            };
        }

        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                boolean isArrayStringValue = PhpElementsUtil.isStringArrayValue().accepts(element);
                if (element == null || !isArrayStringValue || !insideTCAColumnDefinition(element)) {
                    return;
                }

                String arrayKey = extractArrayIndexFromValue(element);
                if (arrayKey != null && arrayKey.equals("allowed") && element instanceof StringLiteralExpression && ((StringLiteralExpression) element).getContents().equals("*")) {
                    return;
                }

                boolean valueIsTableNameFieldValue = arrayIndexIsTCATableNameField(element);
                if (valueIsTableNameFieldValue) {
                    if (element instanceof StringLiteralExpression) {
                        String tableName = ((StringLiteralExpression) element).getContents();
                        boolean isValidTableName = TableUtil.getAvailableTableNames(element.getProject()).contains(tableName);

                        if (!isValidTableName) {
                            problemsHolder.registerProblem(element, "Missing table definition");
                        }
                    }
                }
            }
        };
    }
}
