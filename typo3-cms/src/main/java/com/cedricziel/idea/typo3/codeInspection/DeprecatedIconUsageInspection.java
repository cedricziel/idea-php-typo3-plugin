package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class DeprecatedIconUsageInspection extends PhpInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        if (!TYPO3CMSProjectSettings.getInstance(problemsHolder.getProject()).pluginEnabled) {
            return new PsiElementVisitor() {
            };
        }

        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!stringParameterInMethodReference().accepts(element)) {
                    return;
                }

                StringLiteralExpression literalExpression = (StringLiteralExpression) element;
                String iconIdentifier = literalExpression.getContents();

                if (iconIdentifier.isEmpty()) {
                    return;
                }

                PsiElement methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
                if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getIcon")) {
                    if (IconIndex.getDeprecatedIconIdentifiers(problemsHolder.getProject()).containsKey(iconIdentifier)) {
                        String alternative = IconIndex.getDeprecatedIconIdentifiers(problemsHolder.getProject()).get(iconIdentifier);
                        if (alternative == null || alternative.isEmpty()) {
                            problemsHolder.registerProblem(element, "Deprecated icon usage", ProblemHighlightType.LIKE_DEPRECATED);
                        } else {
                            problemsHolder.registerProblem(element, String.format("Deprecated icon usage - replace with %s", alternative), ProblemHighlightType.LIKE_DEPRECATED, new ReplaceIconIdentifierQuickFix(alternative));
                        }
                    }
                }

                super.visitElement(element);
            }
        };
    }

    private PsiElementPattern.Capture<StringLiteralExpression> stringParameterInMethodReference() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(ParameterList.class);
    }

    private static class ReplaceIconIdentifierQuickFix implements LocalQuickFix {
        private final String alternative;

        public ReplaceIconIdentifierQuickFix(String alternative) {
            this.alternative = alternative;
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            return "Migrate deprecated icon usage";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            if (psiElement instanceof StringLiteralExpression) {
                psiElement.replace(PhpPsiElementFactory.createFromText(project, StringLiteralExpression.class, String.format("'%s'", alternative)));
            }
        }
    }
}
