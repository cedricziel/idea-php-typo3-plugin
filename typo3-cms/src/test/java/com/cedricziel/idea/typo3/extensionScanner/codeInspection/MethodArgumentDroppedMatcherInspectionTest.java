package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class MethodArgumentDroppedMatcherInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/extensionScanner/codeInspection";
    }

    public void testUsageOfDroppedArgumentsInspection() {
        myFixture.enableInspections(new MethodArgumentDroppedMatcherInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("MethodArgumentDroppedMatcher.php", "foo/MethodArgumentDroppedMatcher.php");
        myFixture.copyFileToProject("MethodArgumentDroppedMatcher2.php", "bar/MethodArgumentDroppedMatcher.php");

        myFixture.testHighlighting("deprecated_number_of_arguments_dropped_matching.php");
        myFixture.testHighlighting("deprecated_number_of_arguments_dropped_too_many.php");
    }
}
