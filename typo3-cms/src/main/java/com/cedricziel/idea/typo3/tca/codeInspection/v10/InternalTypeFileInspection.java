package com.cedricziel.idea.typo3.tca.codeInspection.v10;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.TYPO3Utility;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;
import static com.cedricziel.idea.typo3.util.TCAUtil.insideTCAColumnDefinition;

public class InternalTypeFileInspection extends PhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PhpElementVisitor() {
            };
        }

        // the internal type was deprecated on 9 and dropped on 10
        if (TYPO3Utility.compareMajorMinorVersion(problemsHolder.getProject(), "9.0") < 0) {
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
                if (arrayIndex != null && arrayIndex.equals("internal_type")) {
                    if (element instanceof StringLiteralExpression) {
                        String internalType = ((StringLiteralExpression) element).getContents();

                        if (!internalType.equals("file_reference") && !internalType.equals("file")) {
                            return;
                        }

                        // the internal type was deprecated on 9 and dropped on 10
                        if (TYPO3Utility.compareMajorMinorVersion(problemsHolder.getProject(), "10.0") < 0) {
                            problemsHolder.registerProblem(element, String.format("Internal type '%s' is deprecated, will be removed with v10.0", internalType), ProblemHighlightType.LIKE_DEPRECATED);

                            return;
                        }

                        problemsHolder.registerProblem(element, String.format("Internal type '%s' was removed prior to v10.0", internalType), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                }
            }
        };
    }
}
