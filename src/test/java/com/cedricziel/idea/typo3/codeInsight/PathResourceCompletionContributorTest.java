package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.index.ResourcePathIndex;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class PathResourceCompletionContributorTest extends LightCodeInsightFixtureTestCase {
    public void testResourcesAreIndexed() {
        myFixture.addFileToProject("typo3conf/ext/foo/bar.php", "");
        myFixture.addFileToProject("typo3/sysext/baz/bar.png", "");

        assertTrue(ResourcePathIndex.projectContainsResourceFile(myFixture.getProject(), "EXT:foo/bar.php"));
        assertTrue(ResourcePathIndex.projectContainsResourceFile(myFixture.getProject(), "EXT:baz/bar.png"));
    }
}
