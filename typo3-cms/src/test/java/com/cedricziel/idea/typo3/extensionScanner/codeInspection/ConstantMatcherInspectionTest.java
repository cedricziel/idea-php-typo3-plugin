package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class ConstantMatcherInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/extensionScanner/codeInspection";
    }

    public void testUsageOfDeprecatedConstantsIsHighlighted() {
        myFixture.enableInspections(new ConstantMatcherInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ConstantMatcher.php", "foo/ConstantMatcher.php");
        myFixture.copyFileToProject("ConstantMatcher2.php", "bar/ConstantMatcher.php");

        myFixture.testHighlighting("deprecated_constant_usage.php");
    }
}
