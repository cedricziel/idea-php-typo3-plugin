package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;

public class RouteAnnotatorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/annotation";
    }

    public void testDoesntAnnotateRouteWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.copyFileToProject("classes.php");

        myFixture.testHighlighting("missing_route_disabled.php");
    }

    public void testDoesntAnnotateRouteWhenPluginIsEnabledButAnnotatorIsDisabled() {
        TYPO3CMSProjectSettings.getInstance(myFixture.getProject()).routeAnnotatorEnabled = false;

        myFixture.copyFileToProject("classes.php");

        myFixture.testHighlighting("missing_route_disabled.php");
    }

    public void testAnnotatesMissingRoutes() {
        myFixture.copyFileToProject("classes.php");

        myFixture.testHighlighting("missing_route.php");
    }
}
