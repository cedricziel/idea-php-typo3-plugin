package com.cedricziel.idea.typo3;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.LineMarkerProviders;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;

abstract public class AbstractTestCase extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3";
    }

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

    protected void assertResolvesTo(String fileName, String content, Class psiReferenceClass, Class expectedClass) {
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

    protected void assertHasVariant(String fileName, String content, String expectedLookupString, Class translationReferenceClass) {
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


    protected void assertNotHasVariant(String fileName, String content, String expectedLookupString, Class translationReferenceClass) {
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

            final String actualItemText = presentation.getItemText();
            final String actualTailText = presentation.getTailText();
            final String actualTypeText = presentation.getTypeText();
            if (actualItemText.equals(title) && actualTailText.equals(tailText) && actualTypeText.contains(typeText)) {
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

    public void assertLineMarker(@NotNull PsiElement psiElement, @NotNull String markerTooltip) {

        final List<PsiElement> elements = collectPsiElementsRecursive(psiElement);

        for (LineMarkerProvider lineMarkerProvider : LineMarkerProviders.getInstance().allForLanguage(psiElement.getLanguage())) {
            Collection<LineMarkerInfo> lineMarkerInfos = new ArrayList<>();
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos);

            if (lineMarkerInfos.size() == 0) {
                continue;
            }

            for (LineMarkerInfo lineMarkerInfo : lineMarkerInfos) {
                String lineMarkerTooltip = lineMarkerInfo.getLineMarkerTooltip();
                if (lineMarkerTooltip.equals(String.format("<html><body>%s<br></body></html>", markerTooltip))) {
                    return;
                }
            }
        }

        fail(String.format("Line marker %s not found", markerTooltip));
    }

    public void assertLineMarkerIsEmpty(@NotNull PsiElement psiElement) {

        final List<PsiElement> elements = collectPsiElementsRecursive(psiElement);

        for (LineMarkerProvider lineMarkerProvider : LineMarkerProviders.getInstance().allForLanguage(psiElement.getLanguage())) {
            Collection<LineMarkerInfo> lineMarkerInfos = new ArrayList<LineMarkerInfo>();
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos);

            if (lineMarkerInfos.size() > 0) {
                fail(String.format("Fail that line marker is empty because it matches '%s'", lineMarkerProvider.getClass()));
            }
        }
    }

    @NotNull
    private List<PsiElement> collectPsiElementsRecursive(@NotNull PsiElement psiElement) {

        PsiElement[] psiElements = PsiTreeUtil.collectElements(psiElement.getContainingFile(), e -> true);

        return Arrays.asList(psiElements);
    }

    protected void assertNavigationContainsFile(String targetShortcut) {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Set<String> targets = new HashSet<>();

        collectGotoDeclarationTargets(psiElement, targets);

        // its possible to have memory fields,
        // so simple check for ending conditions
        // temp:///src/interchange.en.xlf
        for (String target : targets) {
            if (target.endsWith(targetShortcut)) {
                return;
            }
        }

        fail(String.format("failed that PsiElement (%s) navigate to file %s", psiElement.toString(), targetShortcut));
    }

    protected void assertNavigationNotContainsFile(String path) {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Set<String> targets = new HashSet<>();

        collectGotoDeclarationTargets(psiElement, targets);

        // its possible to have memory fields,
        // so simple check for ending conditions
        // temp:///src/interchange.en.xlf
        for (String target : targets) {
            if (target.endsWith(path)) {
                fail(String.format("failed because PsiElement (%s) navigates to file %s", psiElement.toString(), path));
            }
        }
    }

    private void collectGotoDeclarationTargets(PsiElement psiElement, Set<String> targets) {
        for (GotoDeclarationHandler gotoDeclarationHandler : GotoDeclarationHandler.EP_NAME.getExtensionList()) {
            PsiElement[] gotoDeclarationTargets = gotoDeclarationHandler.getGotoDeclarationTargets(psiElement, 0, myFixture.getEditor());
            if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
                for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                    if (gotoDeclarationTarget instanceof PsiFile) {
                        targets.add(((PsiFile) gotoDeclarationTarget).getVirtualFile().getUrl());
                    }
                }
            }
        }
    }

    public void assertPhpReferenceSignatureContains(LanguageFileType languageFileType, String configureByText, String typeSignature) {
        PsiElement psiElement = assertGetPhpReference(languageFileType, configureByText);
        assertTrue(((PhpReference) psiElement).getSignature().contains(typeSignature));
    }

    @NotNull
    private PsiElement assertGetPhpReference(LanguageFileType languageFileType, String configureByText) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference.class);
        if (psiElement == null) {
            fail("Element is not PhpReference.");
        }

        return psiElement;
    }

    public void assertPhpReferenceResolveTo(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference.class);
        if (psiElement == null) {
            fail("Element is not PhpReference.");
        }

        PsiElement resolve = ((PhpReference) psiElement).resolve();
        if(!pattern.accepts(resolve)) {
            fail(String.format("failed pattern matches element of '%s'", resolve == null ? "null" : resolve.toString()));
        }

        assertTrue(pattern.accepts(resolve));
    }
}
