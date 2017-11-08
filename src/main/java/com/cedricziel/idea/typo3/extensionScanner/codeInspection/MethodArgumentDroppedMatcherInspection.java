package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MethodArgumentDroppedMatcherInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Number of arguments changed with TYPO3 9";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                if (!PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(element)) {
                    return;
                }

                Map<String, Integer> changedMethods = getChangedMethods(element);
                MethodReference methodReference = (MethodReference) element;
                if (methodReference.getClassReference() != null && changedMethods.containsKey(methodReference.getClassReference().getInferredType() + "->" + methodReference.getText())) {
                    problemsHolder.registerProblem(element, "Number of arguments changed with TYPO3 9, consider using an alternative");
                }
            }
        };
    }

    private Map<String, Integer> getChangedMethods(PhpPsiElement element) {
        Set<PsiElement> elements = new HashSet<>();
        PsiFile[] classConstantMatcherFiles = FilenameIndex.getFilesByName(element.getProject(), "MethodArgumentDroppedMatcher.php", GlobalSearchScope.allScope(element.getProject()));
        for (PsiFile file : classConstantMatcherFiles) {

            Collections.addAll(
                    elements,
                    PsiTreeUtil.collectElements(file, el -> PlatformPatterns
                            .psiElement(StringLiteralExpression.class)
                            .withParent(
                                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                                            .withAncestor(
                                                    4,
                                                    PlatformPatterns.psiElement(PhpElementTypes.RETURN)
                                            )
                            )
                            .accepts(el)
                    )
            );
        }

        Map<String, Integer> methodMap = new HashMap<>();
        for (PsiElement gatheredElement : elements) {
            PsiElement[] numberOfArgumentsNumbers = PsiTreeUtil.processElements(
                    gatheredElement,
                    new PsiElementProcessor() {
                        @Override
                        public boolean execute(@NotNull PsiElement element) {
                            return false;
                        }
                    }
            );

            for (PsiElement el : numberOfArgumentsNumbers) {
                methodMap.put("\\" + ((StringLiteralExpression) gatheredElement).getContents(), Integer.parseInt(el.getFirstChild().getFirstChild().getText()));
            }
        }

        return methodMap;
    }
}
