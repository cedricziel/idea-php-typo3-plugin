package com.cedricziel.idea.typo3.extbase.controller;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;

public class ControllerActionReferenceTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");

        myFixture.copyFileToProject("FooController.php", "typo3conf/ext/foo/Controller/FooController.php");
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/extbase";
    }

    public void testReferenceCanResolveDefinition() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "class ActionController {" +
                "public function action() {" +
                "  $this->forward('<caret>baz');" +
                "}" +
                "}"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof ControllerActionReference) {
                ResolveResult[] resolveResults = ((ControllerActionReference) reference).multiResolve(false);
                for (ResolveResult resolveResult : resolveResults) {
                    assertInstanceOf(resolveResult.getElement(), Method.class);

                    return;
                }
            }
        }

        fail("No TranslationReference found");
    }

    public void testReferenceCanResolveVariants() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "class ActionController {" +
                "public function action() {" +
                "  $this->forward('<caret>baz');" +
                "}" +
                "}"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof ControllerActionReference) {
                Object[] variants = reference.getVariants();
                for (Object variant : variants) {
                    if (variant instanceof LookupElement) {
                        String lookupString = ((LookupElement) variant).getLookupString();
                        assertEquals("foo", lookupString);
                        return;
                    }
                }

            }
        }

        fail("No ControllerActionReference found");
    }
}
