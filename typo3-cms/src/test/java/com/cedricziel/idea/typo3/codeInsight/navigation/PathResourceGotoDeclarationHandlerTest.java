package com.cedricziel.idea.typo3.codeInsight.navigation;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class PathResourceGotoDeclarationHandlerTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInsight/navigation";
    }

    public void testCannotGoToDeclarationIfPluginIsDisabled() {
        disablePlugin();

        myFixture.addFileToProject("my_ext/ext_emconf.php", "");
        myFixture.addFileToProject("my_ext/Resources/Public/JavaScripts/resource.js", "");

        myFixture.configureByFile("resource_navigation.php");

        assertNavigationNotContainsFile("my_ext/Resources/Public/JavaScripts/resource.js");
    }

    public void testCanGoToDeclaration() {
        myFixture.addFileToProject("my_ext/ext_emconf.php", "");
        myFixture.addFileToProject("my_ext/Resources/Public/JavaScripts/resource.js", "");

        myFixture.configureByFile("resource_navigation.php");

        assertNavigationContainsFile("my_ext/Resources/Public/JavaScripts/resource.js");
    }
}
