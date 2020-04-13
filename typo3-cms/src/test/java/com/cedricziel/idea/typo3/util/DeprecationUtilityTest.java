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
                myFixture.getProject()).contains("TYPO3\\CMS\\Backend\\Template\\DocumentTemplate::STATUS_ICON_ERROR")
        );
        assertTrue(
            DeprecationUtility.getDeprecatedClassConstants(
                myFixture.getProject()).contains("TYPO3\\CMS\\Backend\\Template\\DocumentTemplate::STATUS_ICON_OK")
        );
        assertFalse(
            DeprecationUtility.getDeprecatedClassConstants(
                myFixture.getProject()).contains("TYPO3\\CMS\\Backend\\Template\\DocumentTemplate::GOOD_CATCH")
        );
    }
}
