package com.cedricziel.idea.typo3.translation.codeInspection.quickfix;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.inspections.quickfix.PhpQuickFixBase;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class CreateMissingTranslationQuickFix extends PhpQuickFixBase {

    private final String key;

    public CreateMissingTranslationQuickFix(@NotNull String key) {
        this.key = key;
    }

    /**
     * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
     * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
     * If the name of the quickfix does not depend on a specific element, simply return getName().
     */
    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Create Translation key";
    }

    /**
     * Called to apply the fix.
     *
     * @param project    {@link Project}
     * @param descriptor problem reported by the tool which provided this quick fix action
     */
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement psiElement = descriptor.getPsiElement();
        if (psiElement instanceof StringLiteralExpression) {
            StringLiteralExpression stringElement = (StringLiteralExpression) psiElement;
            String contents = stringElement.getContents();

            String fileName = TranslationUtil.extractResourceFilenameFromTranslationString(contents);
            String key = TranslationUtil.extractTranslationKeyTranslationString(contents);
            if (fileName != null) {
                PsiElement[] elementsForKey = ResourcePathIndex.findElementsForKey(project, fileName);
                if (elementsForKey.length > 0) {
                    // TranslationUtil.add();
                }
            }
        }
    }
}
