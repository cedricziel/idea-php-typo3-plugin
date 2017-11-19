package com.cedricziel.idea.typo3.index;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.PhpFileType;

import java.util.List;

public class ResourcePathIndexTest extends LightCodeInsightFixtureTestCase {
    public void testResourcesAreIndexed() {
        myFixture.addFileToProject("typo3conf/ext/foo/bar.php", "");
        myFixture.addFileToProject("typo3/sysext/baz/bar.png", "");
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "echo 'EXT:<caret>';");
        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        if (lookupElementStrings == null) {
            fail("Could not complete");
        }

        assertContainsElements(lookupElementStrings, "EXT:foo/bar.php");
        assertContainsElements(lookupElementStrings, "EXT:baz/bar.png");
    }
}
