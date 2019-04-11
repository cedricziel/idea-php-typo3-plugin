package com.cedricziel.idea.typo3.site;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class SiteConfigurationCompletionContributorTest extends AbstractTestCase {

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/site";
    }

    public void testTopLevelRoutingKeysAreNotCompletedWhenPluginDisabled() {
        disablePlugin();

        myFixture.configureByText(
            "config.yaml",
            "l<caret>:"
        );

        assertNotContainsLookupElementWithText(myFixture.completeBasic(), "languages");
    }

    public void testTopLevelRoutingKeysAreCompleted() {
        myFixture.configureByText(
            "config.yaml",
            "l<caret>:"
        );

        assertContainsLookupElementWithText(myFixture.completeBasic(), "languages");
    }

    public void testLanguageKeysAreCompleted() {
        myFixture.configureByText(
            "config.yaml",
            "languages:\n" +
                "  - language<caret>:"
        );

        assertContainsLookupElementWithText(myFixture.completeBasic(), "languageId");
    }

    public void testErrorHandlingKeysAreCompleted() {
        myFixture.configureByText(
            "config.yaml",
            "errorHandling:\n" +
                "  - error<caret>:"
        );

        assertContainsLookupElementWithText(myFixture.completeBasic(), "errorCode");
    }

    public void testRouteKeysAreCompleted() {
        myFixture.configureByText(
            "config.yaml",
            "routes:\n" +
                "  rou<caret>:"
        );

        assertContainsLookupElementWithText(myFixture.completeBasic(), "route");
    }
}
