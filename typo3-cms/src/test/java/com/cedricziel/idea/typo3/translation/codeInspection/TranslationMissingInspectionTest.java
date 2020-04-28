package com.cedricziel.idea.typo3.translation.codeInspection;

import com.cedricziel.idea.typo3.translation.AbstractTranslationTest;

public class TranslationMissingInspectionTest extends AbstractTranslationTest {
    public void testCorrectReferenceIsNotMarked() {
        myFixture.testHighlighting("translation_missing_not_marked.php");
    }

    public void testInvalidReferenceIsMarked() {
        myFixture.testHighlighting("translation_missing_is_marked.php");
    }

    public void testShortenedReferenceIsNotAnnotated() {
        myFixture.testHighlighting("translation_missing_incomplete_not_marked.php");
    }
}
