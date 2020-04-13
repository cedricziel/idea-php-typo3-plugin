package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class ClassNameMatcherInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/extensionScanner/codeInspection";
    }

    public void testUsageOfDeprecatedClassConstantsIsHighlighted() {
        myFixture.enableInspections(new ClassNameMatcherInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ClassNameMatcher.php", "foo/ClassNameMatcher.php");
        myFixture.copyFileToProject("ClassNameMatcher2.php", "bar/ClassNameMatcher.php");

        myFixture.testHighlighting("deprecated_class_name_usage.php");
        myFixture.testHighlighting("deprecated_class_name_usage_import.php");
    }
}
