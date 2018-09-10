package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class IconAnnotatorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/annotation";
    }

    public void testIconsAreNotAnnotatedWhenPluginDisabled() {
        disablePlugin();

        myFixture.copyFileToProject("IconRegistry9.php");
        myFixture.configureByFiles("icon_not_available_disabled.php");

        myFixture.checkHighlighting();
    }

    public void testMissingIconsAreAnnotatedAsWarnings() {
        myFixture.copyFileToProject("IconRegistry9.php");
        myFixture.configureByFiles("icon_not_available.php");

        myFixture.checkHighlighting(true, false, true, false);
    }

    public void testPresentIconsAreNotAnnotated() {
        myFixture.copyFileToProject("IconRegistry9.php");
        myFixture.configureByFiles("icon_available.php");

        myFixture.checkHighlighting();
    }
}
