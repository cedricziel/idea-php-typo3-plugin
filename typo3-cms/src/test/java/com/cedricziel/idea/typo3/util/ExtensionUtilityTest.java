package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class ExtensionUtilityTest extends AbstractTestCase {
    public void testCannotResolveExtensionKeyFromFileWithoutExtension() {
        assertNull(ExtensionUtility.findExtensionKeyFromFile(myFixture.addFileToProject("foo.txt", "").getVirtualFile()));
        assertNull(ExtensionUtility.findExtensionKeyFromFile(myFixture.addFileToProject("typo3conf/ext/foo/foo.txt", "").getVirtualFile()));
    }
}
