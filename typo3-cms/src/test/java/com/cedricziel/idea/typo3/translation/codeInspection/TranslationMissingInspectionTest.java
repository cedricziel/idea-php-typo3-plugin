package com.cedricziel.idea.typo3.translation.codeInspection;

import com.cedricziel.idea.typo3.translation.AbstractTranslationTest;

public class TranslationMissingInspectionTest extends AbstractTranslationTest {
    public void testCorrectReferenceIsNotMarked() {
        assertLocalInspectionNotContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf<caret>:sys_language.language_isocode.ab\";", TranslationMissingInspection.MESSAGE);
    }

    public void testInvalidReferenceIsMarked() {
        assertLocalInspectionContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf<caret>:sys_language.language_isocode.abc\";", TranslationMissingInspection.MESSAGE);
    }

    public void testShortenedReferenceIsNotAnnotated() {
        assertLocalInspectionNotContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf<caret>:\";", TranslationMissingInspection.MESSAGE);
    }
}
