package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;

public class TranslationFoldingBuilderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/translation";
    }

    public void testXMLTranslationsCanBeFolded() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "en";

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xml.xml", "foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("folding_xml_test.php");

        myFixture.testFolding(getTestDataPath() + "/folding_xml_result.php");
    }

    public void testXMLTranslationsCanBeFoldedInNonDefaultLanguage() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "de";

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xml.xml", "foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("folding_nondefault_xml_test.php");

        myFixture.testFolding(getTestDataPath() + "/folding_nondefault_xml_result.php");
    }

    public void testXMLTranslationsAreNotFoldedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xml.xml", "foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("no_folding_xml_test.php");

        myFixture.testFolding(getTestDataPath() + "/no_folding_xml_result.php");
    }

    public void testXMLTranslationsAreNotFoldedWhenDisabledInPluginSettings() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = false;

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xml.xml", "foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("no_folding_xml_test.php");

        myFixture.testFolding(getTestDataPath() + "/no_folding_xml_result.php");
    }

    public void testXLFTranslationsCanBeFolded() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "en";

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xlf.xlf", "foo/Resources/Private/Language/locallang.xlf");

        myFixture.configureByFile("folding_xlf_test.php");

        myFixture.testFolding(getTestDataPath() + "/folding_xlf_result.php");
    }

    public void testXLFTranslationsCanBeFoldedInNonDefaultLanguage() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFoldingLocale = "de";

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xlf.xlf", "foo/Resources/Private/Language/locallang.xlf");

        myFixture.configureByFile("folding_nondefault_xlf_test.php");

        myFixture.testFolding(getTestDataPath() + "/folding_nondefault_xlf_result.php");
    }

    public void testXLFTranslationsAreNotFoldedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xlf.xlf", "foo/Resources/Private/Language/locallang.xlf");

        myFixture.configureByFile("no_folding_xlf_test.php");

        myFixture.testFolding(getTestDataPath() + "/no_folding_xlf_result.php");
    }

    public void testXLFTranslationsAreNotFoldedWhenDisabledInPluginSettings() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = false;

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xlf.xlf", "foo/Resources/Private/Language/locallang.xlf");

        myFixture.configureByFile("no_folding_xlf_test.php");

        myFixture.testFolding(getTestDataPath() + "/no_folding_xlf_result.php");
    }
}
