package com.cedricziel.idea.typo3.index.php;

import com.cedricziel.idea.typo3.util.ExtensionScannerUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class MethodArgumentDroppedMatcherIndexTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/extensionScanner";
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

        myFixture.configureByFile("MethodArgumentDroppedMatcher.php");
    }

    public void testCanIndexMatchingMethodReferences() {
        assertTrue(ExtensionScannerUtil.classMethodHasDroppedArguments(myFixture.getProject(), "\\TYPO3\\CMS\\Core\\Charset\\CharsetConverter->utf8_char_mapping"));
    }

    public void testCanDistinguishNonMatchingMethodReferences() {
        assertFalse(ExtensionScannerUtil.classMethodHasDroppedArguments(myFixture.getProject(), "\\TYPO3\\CMS\\Core\\Charset\\CharsetConverter->non_existent"));
    }

    public void testCanReturnMaximumNumberOfArguments() {
        assertEquals(1, ExtensionScannerUtil.getMaximumNumberOfArguments(myFixture.getProject(), "\\TYPO3\\CMS\\Core\\Charset\\CharsetConverter->utf8_char_mapping"));
    }

    public void testWillReturnNegativeIntegerWhenClassHasNoDroppedArguments() {
        assertEquals(-1, ExtensionScannerUtil.getMaximumNumberOfArguments(myFixture.getProject(), "\\TYPO3\\CMS\\Core\\Charset\\CharsetConverter->non_existent"));
    }
}
