package com.cedricziel.idea.typo3.util;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class TranslationUtilTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> {
            FileTypeManager.getInstance().associateExtension(XmlFileType.INSTANCE, "xlf");
        });

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");

        myFixture.copyFileToProject("sample.xlf", "typo3conf/ext/foo/sample.xlf");
        myFixture.copyFileToProject("de.sample.xlf", "typo3conf/ext/foo/de.sample.xlf");
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/translation";
    }

    public void testCanRecognizeLLLString() {
        assertTrue(TranslationUtil.isTranslationKeyString("LLL:EXT:foo/bar.baz:xml"));
        assertFalse(TranslationUtil.isTranslationKeyString("LL:EXT:foo/bar.baz:xml"));
    }

    public void testCanCheckKeysForExistence() {
        assertTrue(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab"));
        assertTrue(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab"));
        assertFalse(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.abdks"));
    }

    public void testCanExtractExtensionKeyFromTranslationString() {
        assertEquals("foo", TranslationUtil.extractResourceFilenameFromTranslationString("LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab"));
    }

    public void testCanFindDefinitionElements() {
        assertSize(2, TranslationUtil.findDefinitionElements(myFixture.getProject(), "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab"));
        assertSize(1, TranslationUtil.findDefinitionElements(myFixture.getProject(), "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab"));

        PsiElement[] definitionElements = TranslationUtil.findDefinitionElements(myFixture.getProject(), "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab");
        for (PsiElement definitionElement : definitionElements) {
            assertInstanceOf(definitionElement, XmlAttributeValue.class);

            XmlTag tag = (XmlTag) definitionElement;
            assertEquals("In case the element is XLIFF, the tag name is trans-unit", "trans-unit", tag.getName());
        }
    }
}
