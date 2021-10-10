package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.arrayIndexIsTCATableNameField;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class MissingTableInspection extends PluginEnabledPhpInspection {
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
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
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
                if (!valueIsTableNameFieldValue) {
                    return;
                }

                if (element instanceof StringLiteralExpression) {
                    List<String> tableNames = new ArrayList<>();

                    String content = ((StringLiteralExpression) element).getContents();

                    if (arrayKey != null && arrayKey.equals("allowed")) {
                        tableNames = Arrays.asList(content.trim().split("\\s*,\\s*"));
                    } else {
                        tableNames.add(content);
                    }

                    for (String tableName : tableNames) {
                        boolean isValidTableName = TableUtil.getAvailableTableNames(element.getProject()).contains(tableName);
                        if (!isValidTableName) {
                            int i = content.indexOf(tableName);
                            problemsHolder.registerProblem(element, new TextRange(i + 1, i + 1 + tableName.length()), String.format("Table '%s' is not defined", tableName));
                        }
                    }
                }
            }
        };
    }
}
