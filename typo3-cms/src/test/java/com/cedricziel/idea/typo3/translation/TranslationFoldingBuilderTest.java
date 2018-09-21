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
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFavoriteLocale = "en";

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("folding_xml.xml", "foo/Resources/Private/Language/locallang.xml");

        myFixture.configureByFile("folding_xml_test.php");

        myFixture.testFolding(getTestDataPath() + "/folding_xml_result.php");
    }

    public void testXMLTranslationsCanBeFoldedInNonDefaultLanguage() {
        TYPO3CMSProjectSettings.getInstance(getProject()).translationEnableTextFolding = true;
        TYPO3CMSProjectSettings.getInstance(getProject()).translationFavoriteLocale = "de";

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
}
