package com.cedricziel.idea.typo3.index;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class IconIndexTest extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/icons";
    }

    public void testIconsAreDetectedForv7() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry7.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }

    public void testIconsAreDetectedForv8() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry8.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }

    public void testIconsAreDetectedForv9() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry9.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-wizard-link");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }
}
