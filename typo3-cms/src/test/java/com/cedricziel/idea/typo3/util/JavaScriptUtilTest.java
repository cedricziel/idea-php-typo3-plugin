package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiFile;

public class JavaScriptUtilTest extends AbstractTestCase {
    public void testExtensionNameConversion() {
        assertEquals("Backend", JavaScriptUtil.normalizeExtensionKeyForJs("backend"));
        assertEquals("MyMagic", JavaScriptUtil.normalizeExtensionKeyForJs("my_magic"));
    }

    public void testCanCreateModuleNameFromFile() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        PsiFile jsFile = myFixture.addFileToProject("foo/" + JavaScriptUtil.SIGNIFICANT_PATH + "/Bar.js", "");

        assertEquals("TYPO3/CMS/Foo/Bar", JavaScriptUtil.calculateModuleName(jsFile));
    }

    public void testCanCreateModuleNameFromFile_subpath() {
        myFixture.addFileToProject("my_magic_thing/ext_emconf.php", "");
        PsiFile jsFile = myFixture.addFileToProject("my_magic_thing/" + JavaScriptUtil.SIGNIFICANT_PATH + "/Ding/Dong/Bar.js", "");

        assertEquals("TYPO3/CMS/MyMagicThing/Ding/Dong/Bar", JavaScriptUtil.calculateModuleName(jsFile));
    }
}
