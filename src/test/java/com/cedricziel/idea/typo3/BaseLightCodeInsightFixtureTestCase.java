package com.cedricziel.idea.typo3;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseLightCodeInsightFixtureTestCase extends LightCodeInsightFixtureTestCase {

    public void assertLocalInspectionContains(String filename, String content, String contains) {
        Set<String> matches = new HashSet<>();

        Pair<List<ProblemDescriptor>, Integer> localInspectionsAtCaret = getLocalInspectionsAtCaret(filename, content);
        for (ProblemDescriptor result : localInspectionsAtCaret.getFirst()) {
            TextRange textRange = result.getPsiElement().getTextRange();
            if (textRange.contains(localInspectionsAtCaret.getSecond()) && result.toString().equals(contains)) {
                return;
            }

            matches.add(result.toString());
        }

        fail(String.format("Fail matches '%s' with one of %s", contains, matches));
    }

    private Pair<List<ProblemDescriptor>, Integer> getLocalInspectionsAtCaret(@NotNull String filename, @NotNull String content) {

        PsiElement psiFile = myFixture.configureByText(filename, content);

        int caretOffset = myFixture.getCaretOffset();
        if (caretOffset <= 0) {
            fail("Please provide <caret> tag");
        }

        ProblemsHolder problemsHolder = new ProblemsHolder(InspectionManager.getInstance(getProject()), psiFile.getContainingFile(), false);

        for (LocalInspectionEP localInspectionEP : LocalInspectionEP.LOCAL_INSPECTION.getExtensions()) {
            Object object = localInspectionEP.getInstance();
            if (!(object instanceof LocalInspectionTool)) {
                continue;
            }

            final PsiElementVisitor psiElementVisitor = ((LocalInspectionTool) object).buildVisitor(problemsHolder, false);

            psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    psiElementVisitor.visitElement(element);
                    super.visitElement(element);
                }
            });

            psiElementVisitor.visitFile(psiFile.getContainingFile());
            ;
        }

        return Pair.create(problemsHolder.getResults(), caretOffset);
    }
}
