package com.cedricziel.idea.typo3.index.php;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class LegacyClassesForIDEIndexTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index";
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

        myFixture.configureByFile("LegacyClassesForIde.php");
    }

    public void testLegacyClassesForIDEFilesAreIndexed() {
        assertTrue(LegacyClassesForIDEIndex.isLegacyClass(myFixture.getProject(), "\\TYPO3\\CMS\\Fluid\\Core\\Exception"));
        assertTrue(LegacyClassesForIDEIndex.isLegacyClass(myFixture.getProject(), "\\TYPO3\\CMS\\Fluid\\Core\\ViewHelper\\ViewHelperInterface"));
    }

    public void testReplacementClassNamesCanBeRetrieved() {
        assertEquals(LegacyClassesForIDEIndex.findReplacementClass(myFixture.getProject(), "\\TYPO3\\CMS\\Fluid\\Core\\ViewHelper\\ViewHelperInterface"), "\\TYPO3Fluid\\Fluid\\Core\\ViewHelper\\ViewHelperInterface");
        assertNull(LegacyClassesForIDEIndex.findReplacementClass(myFixture.getProject(), "\\TYPO3\\CMS\\NoRealClass"));
    }
}
