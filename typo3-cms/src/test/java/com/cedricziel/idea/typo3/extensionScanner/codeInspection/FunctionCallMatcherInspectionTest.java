package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class FunctionCallMatcherInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/extensionScanner/codeInspection";
    }

    public void testUsageOfDeprecatedFunctionCalls() {
        myFixture.enableInspections(new FunctionCallMatcherInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("FunctionCallMatcher.php", "foo/FunctionCallMatcher.php");
        myFixture.copyFileToProject("FunctionCallMatcher2.php", "bar/FunctionCallMatcher.php");

        myFixture.testHighlighting("deprecated_function_call.php");
    }
}
