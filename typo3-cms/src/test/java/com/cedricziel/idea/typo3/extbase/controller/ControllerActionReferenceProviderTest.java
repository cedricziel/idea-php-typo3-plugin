package com.cedricziel.idea.typo3.extbase.controller;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.PhpFileType;

public class ControllerActionReferenceProviderTest extends LightCodeInsightFixtureTestCase {
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

    public void testCanCreateTranslationReferencesOnPhpStrings() {
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
                return;
            }
        }

        fail("No ControllerActionReference found");
    }
}
