package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileTypeManager;

public class TranslatorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/translation";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> FileTypeManager.getInstance().associateExtension(XmlFileType.INSTANCE, "xlf"));

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");
    }

    public void testWillReturnNullOnEmptyResult() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "en";

        myFixture.copyFileToProject("folding_xml.xml", "typo3conf/ext/foo/Resources/Private/Language/locallang.xml");

        assertNull(Translator.translateLLLString(myFixture.getProject(), "LLL:EXT:foo/Resources/Private/Language/locallang.xlf:sys_language.language_isocode.ab"));
    }

    public void testCanTranslateLLLString() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "en";

        myFixture.copyFileToProject("folding_xml.xml", "typo3conf/ext/foo/Resources/Private/Language/locallang.xml");

        assertEquals("English", Translator.translateLLLString(myFixture.getProject(), "LLL:EXT:foo/Resources/Private/Language/locallang.xml:mylabel"));
    }

    public void testCanTranslateLLLStringInNonDefaultXMLLanguage() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "de";

        myFixture.copyFileToProject("folding_xml.xml", "typo3conf/ext/foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("folding_nondefault_xml_test.php");

        assertEquals("Deutsch", Translator.translateLLLString(myFixture.getProject(), "LLL:EXT:foo/Resources/Private/Language/locallang.xml:mylabel"));
    }

    public void testCanTranslateLLLStringXLF() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "en";

        myFixture.copyFileToProject("folding_xlf.xlf", "typo3conf/ext/foo/Resources/Private/Language/locallang.xlf");

        assertEquals("Abkhaz", Translator.translateLLLString(myFixture.getProject(), "LLL:EXT:foo/Resources/Private/Language/locallang.xlf:sys_language.language_isocode.ab"));
    }

    public void testCanTranslateLLLStringXLFNonDefaultLanguage() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "de";

        myFixture.copyFileToProject("folding_xlf.xlf", "typo3conf/ext/foo/Resources/Private/Language/locallang.xlf");

        assertEquals("DE - Abkhaz", Translator.translateLLLString(myFixture.getProject(), "LLL:EXT:foo/Resources/Private/Language/locallang.xlf:sys_language.language_isocode.ab"));
    }
}
