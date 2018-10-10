package com.cedricziel.idea.typo3.index;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class IconIndexTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/icons";
    }

    public void testIconsAreDetectedForv7() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry7.php");

        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release"));
        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg"));
    }

    public void testIconsAreDetectedForv8() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry8.php");

        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release"));
        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg"));
    }

    public void testIconsAreDetectedForv9() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry9.php");

        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-wizard-link"));
        assertTrue(IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg"));
    }

    public void testDeprecatedIconsAreDetectedForv9() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry9.php");

        assertTrue(IconIndex.getDeprecatedIconIdentifiers(myFixture.getProject()).containsKey("status-status-edit-read-only"));
        assertEquals(IconIndex.getDeprecatedIconIdentifiers(myFixture.getProject()).get("status-status-edit-read-only"), "status-edit-read-only");
    }
}
