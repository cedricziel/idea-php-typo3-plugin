package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.translation.AbtractTranslationTest;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;

public class TranslationUtilTest extends AbtractTranslationTest {
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
        }
    }
}
