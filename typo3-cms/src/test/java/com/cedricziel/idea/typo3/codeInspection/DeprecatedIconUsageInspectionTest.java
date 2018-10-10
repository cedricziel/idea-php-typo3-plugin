package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class DeprecatedIconUsageInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/icons";
    }

    public void testInspectionIsDisabledWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("IconRegistry9.php");

        myFixture.enableInspections(DeprecatedIconUsageInspection.class);

        myFixture.configureByFile("deprecated_icon_off.php");

        myFixture.testHighlighting();
    }

    public void testInspectionHighlightsDeprecatedIconUsage() {
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("IconRegistry9.php");

        myFixture.enableInspections(DeprecatedIconUsageInspection.class);

        myFixture.configureByFile("deprecated_icon.php");

        myFixture.testHighlighting();
    }
}
