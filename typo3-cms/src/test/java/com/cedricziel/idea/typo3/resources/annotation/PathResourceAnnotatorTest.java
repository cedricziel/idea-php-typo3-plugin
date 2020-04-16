package com.cedricziel.idea.typo3.resources.annotation;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class PathResourceAnnotatorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/resources/annotation";
    }

    public void testMissingResourceIsHighlighted() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");

        myFixture.testHighlighting("missing_resource.php");
    }

    public void testExistingFileResourceIsNotHighlighted() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.addFileToProject("foo/icon.gif", "");

        myFixture.testHighlighting("existing_file_resource_is_not_highlighted.php");
    }

    public void testExistingFolderResourceIsNotHighlighted() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.addFileToProject("foo/icon.gif", "");
        myFixture.addFileToProject("foo/Bar/.gitkeep", "");

        myFixture.testHighlighting("existing_folder_resource_is_not_highlighted.php");
    }
}
