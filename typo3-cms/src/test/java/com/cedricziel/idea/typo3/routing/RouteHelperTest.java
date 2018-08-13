package com.cedricziel.idea.typo3.routing;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.jetbrains.php.lang.PhpFileType;

import java.util.List;

public class RouteHelperTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/routing";
    }

    public void testCanCorrectlyIdentifyRoutesFromRoutesPhp() {
        myFixture.copyFileToProject("AjaxRoutes.php");
        myFixture.copyFileToProject("Routes.php");

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $uriBuilder \\TYPO3\\CMS\\Backend\\Routing\\UriBuilder */\n" +
            "$uriBuilder->buildUriFromRoute('<caret>');"
        );

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue(lookupElementStrings.contains("ajax_solr_updateConnections"));
        assertTrue(lookupElementStrings.contains("ajax_solr_updateConnection"));
        assertTrue(lookupElementStrings.contains("xMOD_tximpexp"));
    }
}
