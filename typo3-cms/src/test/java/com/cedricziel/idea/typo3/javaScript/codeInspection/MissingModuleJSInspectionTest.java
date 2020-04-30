package com.cedricziel.idea.typo3.javaScript.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;

public class MissingModuleJSInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/javaScript/codeInspection";
    }

    public void testExistingModuleIsNotMarked() {
        myFixture.enableInspections(new MissingModuleJSInspection());

        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");
        myFixture.addFileToProject("foo_bar/Resources/Public/JavaScript/Baz.js", "");

        myFixture.testHighlighting("use_reference.js");
    }

    public void testMissingModuleIsMarked() {
        myFixture.enableInspections(new MissingModuleJSInspection());

        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");

        myFixture.testHighlighting("use_reference_missing.js");
    }
}
