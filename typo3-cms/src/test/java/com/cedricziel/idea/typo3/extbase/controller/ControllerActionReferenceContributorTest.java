package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpFileType;

public class ControllerActionReferenceContributorTest extends AbstractTestCase {
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
        assertPsiElementHasReferenceOfType(elementAtCaret, ControllerActionReference.class);
    }

    public void testCanCreateReferenceOnConfigurePluginCallsWithControllerClassConstant() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
            "defined('TYPO3_MODE') or die();\n" +
            "\n" +
            "namespace OliverHader\\IrreTutorial\\Controller {\n" +
            "class ContentController {}\n" +
            "    public function createAction() {}\n" +
            "}\n" +
            "\\TYPO3\\CMS\\Extbase\\Utility\\ExtensionUtility::configurePlugin(\n" +
            "    'IrreTutorial',\n" +
            "    'Irre',\n" +
            "    [\n" +
            "        \\OliverHader\\IrreTutorial\\Controller\\QueueController::class => 'index',\n" +
            "        \\OliverHader\\IrreTutorial\\Controller\\ContentController::class => 'list, show, new, c<caret>reate, edit, update, delete'\n" +
            "    ],\n" +
            "    [\n" +
            "        \\OliverHader\\IrreTutorial\\Controller\\ContentController::class => 'create, update, delete'\n" +
            "    ]\n" +
            ");"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        assertPsiElementHasReferenceOfType(elementAtCaret, ControllerActionReference.class);
    }
}
