package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.Triple;
import com.cedricziel.idea.typo3.translation.AbstractTranslationTest;
import com.cedricziel.idea.typo3.translation.StubTranslation;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TranslationIndexTest extends AbstractTranslationTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/translation";
    }

    public void testResourcesAreIndexed() {
        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, getProject());

        // may fail until we can register the xlf file extension conditionally
        assertContainsElements(allKeys, "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab");
        assertContainsElements(allKeys, "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab");
    }

    public void testIndexesInMultipleLanguagesAreAvailableMultipleTimes() {
        assertTrue(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab"));
        assertTrue(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab"));
        assertFalse(TranslationUtil.keyExists(myFixture.getProject(), "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.baz"));
    }

    public void testStubModelIsBuiltCorrectly() {
        List<StubTranslation> stubsById = TranslationIndex.findById(myFixture.getProject(), "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab");

        assertSize(2, stubsById);

        stubsById.forEach(stubTranslation -> {
            switch (stubTranslation.getId()) {
                case "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab":
                    assertNotNull("A stub model is available from the index ", stubTranslation);
                    assertEquals("The extension key is correctly resolved when building the index", stubTranslation.getExtension(), "foo");
                    assertEquals("The id local index is resolved correctly", "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab", stubTranslation.getId());
                    assertEquals("The default language is resolved correctly", "en", stubTranslation.getLanguage());
                    assertNotNull("The text range is saved on the stub to resolve the element later", stubTranslation.getTextRange());
                    break;
                case "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab":
                    assertNotNull("A stub model is available from the index ", stubTranslation);
                    assertEquals("The extension key is correctly resolved when building the index", stubTranslation.getExtension(), "foo");
                    assertEquals("The id local index is resolved correctly", "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab", stubTranslation.getId());
                    assertEquals("The default language is resolved correctly", "de", stubTranslation.getLanguage());
                    assertNotNull("The text range is saved on the stub to resolve the element later", stubTranslation.getTextRange());
                    break;
                default:
                    fail("Unexpected element.");
                    break;
            }
        });
    }

    public void testCanFindAllTranslationStubs() {
        assertSize(3, TranslationIndex.findAllTranslationStubs(myFixture.getProject()));
    }

    public void testCanFindAllTranslationIds() {
        myFixture.copyFileToProject("multiple_languages.xml", "typo3conf/ext/foo/locallang.xml");

        Collection<String> allTranslations = TranslationIndex.findAllKeys(myFixture.getProject());

        assertSize(3, allTranslations);
        assertContainsElements(
                allTranslations,
                "LLL:EXT:foo/de.sample.xlf:sys_language.language_isocode.ab",
                "LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab"
        );
    }

    public void testCanFindMultipleLanguagesPerXMLFile() {
        myFixture.copyFileToProject("multiple_languages.xml", "typo3conf/ext/foo/locallang.xml");

        List<Triple<String, String, String>> triples = new ArrayList<>() {{
            add(new Triple<>("default", "mylabel", "English"));
            add(new Triple<>("de", "mylabel", "Deutsch"));
            add(new Triple<>("fr", "mylabel", "Français"));
            add(new Triple<>("nl", "mylabel", "Nederlands"));
            add(new Triple<>("es", "mylabel", "Español"));
        }};

        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, getProject());

        // may fail until we can register the xlf file extension conditionally
        assertContainsElements(allKeys, "LLL:EXT:foo/locallang.xml:mylabel");

        List<StubTranslation> stubsById = TranslationIndex.findById(myFixture.getProject(), "LLL:EXT:foo/locallang.xml:mylabel");

        for (Triple<String, String, String> triple : triples) {
            assertTranslationStubExistsInList(stubsById, triple.getLeft(), triple.getMiddle(), triple.getRight());
        }
    }

    public void testIssue285NullPointerIsAvoided() {
        myFixture.copyFileToProject("locallang_be.xlf", "typo3conf/ext/news/Resources/Private/Language/locallang_be.xlf");
    }

    private void assertTranslationStubExistsInList(List<StubTranslation> stubsById, String left, String middle, String right) {
        for (StubTranslation stubTranslation : stubsById) {
            if (stubTranslation.getLanguage().equals(left)) {
                return;
            }
        }

        fail("Could not find translation stub in list");
    }
}
