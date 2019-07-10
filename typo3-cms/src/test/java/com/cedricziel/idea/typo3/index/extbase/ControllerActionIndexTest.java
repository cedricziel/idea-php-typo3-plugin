package com.cedricziel.idea.typo3.index.extbase;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class ControllerActionIndexTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/extbase";
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");
        myFixture.copyFileToProject("FooController.php", "typo3conf/ext/foo/Controller/FooController.php");
    }

    public void testControllerActionsAreIndexed() {
        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(ControllerActionIndex.KEY, myFixture.getProject());

        assertTrue(allKeys.contains("baz"));
    }
}
