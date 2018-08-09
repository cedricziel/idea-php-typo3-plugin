package com.cedricziel.idea.typo3.translation.annotation;

import com.cedricziel.idea.typo3.translation.AbtractTranslationTest;

public class TranslationAnnotatorTest extends AbtractTranslationTest {
    public void testCorrectReferenceIsNotMarked() {
        assertAnnotationNotContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";", "Unresolved translation");
    }

    public void testInvalidReferenceIsAnnotated() {
        assertAnnotationContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.abc\";", "Unresolved translation");
    }

    public void testShortenedReferenceIsNotAnnotated() {
        assertAnnotationNotContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";", "Unresolved translation");
    }

    public void testConcatenationIsNotAnnotated() {
        assertAnnotationNotContains("foo.php", "<?php \n\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.\" . \"ab\";", "Unresolved translation");
    }
}
