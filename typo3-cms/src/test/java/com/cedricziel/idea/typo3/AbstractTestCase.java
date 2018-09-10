package com.cedricziel.idea.typo3;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInspection.*;
import com.intellij.lang.LanguageAnnotators;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract public class AbstractTestCase extends LightCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        prepareSettings();
    }

    protected void prepareSettings() {
        TYPO3CMSProjectSettings.getInstance(myFixture.getProject()).pluginEnabled = true;
    }

    protected void disablePlugin() {
        TYPO3CMSProjectSettings.getInstance(myFixture.getProject()).pluginEnabled = false;
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
        }

        return Pair.create(problemsHolder.getResults(), caretOffset);
    }

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

    public void assertAnnotationContains(String filename, String content, String contains) {
        List<String> matches = new ArrayList<>();
        for (Annotation annotation : getAnnotationsAtCaret(filename, content)) {
            matches.add(annotation.toString());
            if (annotation.getMessage().contains(contains)) {
                return;
            }
        }

        fail(String.format("Fail matches '%s' with one of %s", contains, matches));
    }

    public void assertLocalInspectionNotContains(String filename, String content, String contains) {
        Pair<List<ProblemDescriptor>, Integer> localInspectionsAtCaret = getLocalInspectionsAtCaret(filename, content);

        for (ProblemDescriptor result : localInspectionsAtCaret.getFirst()) {
            TextRange textRange = result.getPsiElement().getTextRange();
            if (textRange.contains(localInspectionsAtCaret.getSecond()) && result.toString().contains(contains)) {
                fail(String.format("Fail inspection not contains '%s'", contains));
            }
        }
    }

    public void assertAnnotationNotContains(String filename, String content, String contains) {
        for (Annotation annotation : getAnnotationsAtCaret(filename, content)) {
            if (annotation.getMessage() != null && annotation.getMessage().contains(contains)) {
                fail(String.format("Fail not matching '%s' with '%s'", contains, annotation));
            }
        }
    }

    @NotNull
    private AnnotationHolderImpl getAnnotationsAtCaret(String filename, String content) {
        PsiFile psiFile = myFixture.configureByText(filename, content);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        AnnotationHolderImpl annotations = new AnnotationHolderImpl(new AnnotationSession(psiFile));

        for (Annotator annotator : LanguageAnnotators.INSTANCE.allForLanguage(psiFile.getLanguage())) {
            annotator.annotate(psiElement, annotations);
        }

        return annotations;
    }

    protected void assertResolvesTo(String fileName, String content, Class psiReferenceClass, Class expectedClass, String s) {
        PsiFile file = myFixture.configureByText(fileName, content);
        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (psiReferenceClass.isInstance(reference)) {
                ResolveResult[] resolveResults = ((PsiPolyVariantReference) reference).multiResolve(false);
                for (ResolveResult resolveResult : resolveResults) {
                    assertInstanceOf(resolveResult.getElement(), expectedClass);

                    return;
                }
            }
        }

        fail(String.format("Reference %s does not resolve to correct element", psiReferenceClass.getName()));
    }

    protected void assertHasVariant(String fileName, String content, String expectedLookupString, Class translationReferenceClass, String message) {
        PsiFile file = myFixture.configureByText(fileName, content);
        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (translationReferenceClass.isInstance(reference)) {
                Object[] variants = reference.getVariants();
                for (Object variant : variants) {
                    if (variant instanceof LookupElement) {
                        String lookupString = ((LookupElement) variant).getLookupString();
                        if (lookupString.equals(expectedLookupString)) {
                            return;
                        }
                    }
                }

            }
        }

        fail(String.format("%s could not resolve variant %s", translationReferenceClass.getName(), expectedLookupString));
    }


    protected void assertNotHasVariant(String fileName, String content, String expectedLookupString, Class translationReferenceClass, String message) {
        PsiFile file = myFixture.configureByText(fileName, content);
        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (translationReferenceClass.isInstance(reference)) {
                Object[] variants = reference.getVariants();
                for (Object variant : variants) {
                    if (variant instanceof LookupElement) {
                        String lookupString = ((LookupElement) variant).getLookupString();
                        if (lookupString.equals(expectedLookupString)) {
                            fail(String.format("%s could not resolve variant %s", translationReferenceClass.getName(), expectedLookupString));
                        }
                    }
                }

            }
        }
    }

    protected void assertContainsLookupElementWithText(LookupElement[] lookupElements, String title) {
        for (LookupElement lookupElement : lookupElements) {
            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);
            if (presentation.getItemText().equals(title)) {
                return;
            }
        }

        fail("No such element");
    }

    protected void assertContainsLookupElementWithText(LookupElement[] lookupElements, @NotNull String title, @NotNull String tailText, @NotNull String typeText) {
        for (LookupElement lookupElement : lookupElements) {
            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);
            if (presentation.getItemText().equals(title) && presentation.getTailText().equals(tailText) && presentation.getTypeText().contains(typeText)) {
                return;
            }
        }

        fail("No such element");
    }

    protected void assertNotContainsLookupElementWithText(LookupElement[] lookupElements, @NotNull String title) {
        for (LookupElement lookupElement : lookupElements) {
            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);
            if (presentation.getItemText().equals(title)) {
                fail("Element shouldnt be present but is");
            }
        }
    }

    protected void assertContainsReference(@NotNull Class c, @NotNull PsiElement element) {
        for (PsiReference reference : element.getReferences()) {
            if (reference.getClass().equals(c)) {
                return;
            }
        }

        fail(String.format("Could not find reference of type %s", c.getName()));
    }
}
