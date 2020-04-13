package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class ClassConstantMatcherInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/extensionScanner/codeInspection";
    }

    public void testUsageOfDeprecatedClassConstantsIsHighlighted() {
        myFixture.enableInspections(new ClassConstantMatcherInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ClassConstantMatcher.php", "foo/ClassConstantMatcher.php");
        myFixture.copyFileToProject("ClassConstantMatcher2.php", "bar/ClassConstantMatcher.php");

        myFixture.testHighlighting("deprecated_constant_usage.php");
    }
}
