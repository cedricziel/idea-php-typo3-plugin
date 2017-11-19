package com.cedricziel.idea.typo3.index;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class ResourcePathIndexTest extends LightCodeInsightFixtureTestCase {
    public void testResourcesAreIndexed() {
        myFixture.addFileToProject("typo3conf/ext/foo/bar.php", "");
        myFixture.addFileToProject("typo3/sysext/baz/bar.png", "");

        assertTrue(ResourcePathIndex.projectContainsResourceFile(myFixture.getProject(), "EXT:foo/bar.php"));
        assertTrue(ResourcePathIndex.projectContainsResourceFile(myFixture.getProject(), "EXT:baz/bar.png"));
    }

    public void testInstallerNameUnconventionalResourcePathsAreDetected() {

        // extension key "cascaded" resolved through extra.typo3/cms.extension-key
        myFixture.addFileToProject("cc/ext_emconf.php", "");
        myFixture.addFileToProject("cc/composer.json", "{\n" +
                "\t\"name\": \"package/foo\",\n" +
                "\t\"bar\": \"baz\",\n" +
                "\t\"type\": \"typo3-cms-extension\",\n" +
                "\t\"extra\": {\n" +
                "\t\t\"installerName\": \"foo_bar_baz\",\n" +
                "\t\t\"typo3/cms\": {\n" +
                "\t\t\t\"extension-key\": \"cascaded\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n");

        // extension key "bar" resolved through package name
        myFixture.addFileToProject("bar_bz/ext_emconf.php", "");
        myFixture.addFileToProject("bar_bz/composer.json", "{\n" +
                "\t\"name\": \"package/bar\",\n" +
                "\t\"bar\": \"baz\",\n" +
                "\t\"type\": \"typo3-cms-extension\",\n" +
                "\t\"extra\": {\n" +
                "\t}\n" +
                "}\n");

        // extension key "foo_bar_baz" resolved through the extras.installerName property
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.addFileToProject("foo/composer.json", "{\n" +
                "\t\"name\": \"package/foo\",\n" +
                "\t\"bar\": \"baz\",\n" +
                "\t\"type\": \"typo3-cms-extension\",\n" +
                "\t\"extra\": {\n" +
                "\t\t\"installerName\": \"foo_bar_baz\"\n" +
                "\t}\n" +
                "}\n");

        // extension key "bingo" resolved through the folder
        myFixture.addFileToProject("bingo/ext_emconf.php", "");

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "echo 'EXT:<caret>';");
        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        if (lookupElementStrings == null) {
            fail("Could not complete");
        }

        assertContainsElements(lookupElementStrings, "EXT:cascaded/ext_emconf.php");
        assertContainsElements(lookupElementStrings, "EXT:bingo/ext_emconf.php");
        assertContainsElements(lookupElementStrings, "EXT:foo_bar_baz/ext_emconf.php");
        assertContainsElements(lookupElementStrings, "EXT:bar/ext_emconf.php");
    }
}
