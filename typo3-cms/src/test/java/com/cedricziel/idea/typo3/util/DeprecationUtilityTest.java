package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class DeprecationUtilityTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/util";
    }

    public void testDeprecatedClassConstantsCanBeFound() {
        myFixture.copyFileToProject("ClassConstantMatcher.php", "foo/ClassConstantMatcher.php");
        myFixture.copyFileToProject("ClassConstantMatcher2.php", "bar/ClassConstantMatcher.php");

        assertTrue(
            DeprecationUtility.getDeprecatedClassConstants(
                myFixture.getProject()).contains("\\TYPO3\\CMS\\Backend\\Template\\DocumentTemplate.STATUS_ICON_ERROR")
        );
        assertTrue(
            DeprecationUtility.getDeprecatedClassConstants(
                myFixture.getProject()).contains("\\TYPO3\\CMS\\Backend\\Template\\DocumentTemplate.STATUS_ICON_OK")
        );
        assertFalse(
            DeprecationUtility.getDeprecatedClassConstants(
                myFixture.getProject()).contains("\\TYPO3\\CMS\\Backend\\Template\\DocumentTemplate.GOOD_CATCH")
        );
    }

    public void testDeprecatedClassNamesCanBeFound() {
        myFixture.copyFileToProject("ClassNameMatcher.php", "foo/ClassNameMatcher.php");
        myFixture.copyFileToProject("ClassNameMatcher2.php", "bar/ClassNameMatcher.php");

        assertTrue(
            DeprecationUtility.getDeprecatedClassNames(
                myFixture.getProject()).contains("\\RemoveXSS")
        );
        assertTrue(
            DeprecationUtility.getDeprecatedClassNames(
                myFixture.getProject()).contains("\\TYPO3\\CMS\\Backend\\Console\\CliRequestHandler")
        );
        assertFalse(
            DeprecationUtility.getDeprecatedClassNames(
                myFixture.getProject()).contains("\\App\\Apple")
        );
    }

    public void testDeprecatedConstantsCanBeFound() {
        myFixture.copyFileToProject("ConstantMatcher.php", "foo/ConstantMatcher.php");
        myFixture.copyFileToProject("ConstantMatcher2.php", "bar/ConstantMatcher.php");

        assertTrue(
            DeprecationUtility.getDeprecatedConstantNames(
                myFixture.getProject()).contains("\\TYPO3_DLOG")
        );
        assertTrue(
            DeprecationUtility.getDeprecatedConstantNames(
                myFixture.getProject()).contains("\\TYPO3_user_agent")
        );
        assertFalse(
            DeprecationUtility.getDeprecatedConstantNames(
                myFixture.getProject()).contains("\\BAR")
        );
    }

    public void testDeprecatedFunctionCallsCanBeFound() {
        myFixture.copyFileToProject("FunctionCallMatcher.php", "foo/FunctionCallMatcher.php");
        myFixture.copyFileToProject("FunctionCallMatcher2.php", "bar/FunctionCallMatcher.php");

        assertTrue(
            DeprecationUtility.getDeprecatedGlobalFunctionCalls(
                myFixture.getProject()).contains("\\debugEnd")
        );
        assertTrue(
            DeprecationUtility.getDeprecatedGlobalFunctionCalls(
                myFixture.getProject()).contains("\\debug")
        );
        assertFalse(
            DeprecationUtility.getDeprecatedGlobalFunctionCalls(
                myFixture.getProject()).contains("\\file_get_contents")
        );
    }
}
