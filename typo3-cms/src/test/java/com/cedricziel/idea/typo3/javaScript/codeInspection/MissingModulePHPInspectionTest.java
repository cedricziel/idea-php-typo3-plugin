package com.cedricziel.idea.typo3.javaScript.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import junit.framework.TestCase;

public class MissingModulePHPInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/javaScript/codeInspection";
    }

    public void testUnknownModulesAreHighlighted() {
        myFixture.enableInspections(new MissingModulePHPInspection());

        myFixture.copyFileToProject("classes.php");

        myFixture.testHighlighting("js_module_reference_invalid.php");
    }

    public void testKnownModulesAreNotHighlighted() {
        myFixture.enableInspections(new MissingModulePHPInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");
        myFixture.addFileToProject("foo_bar/Resources/Public/JavaScript/Baz.js", "");

        myFixture.testHighlighting("js_module_reference.php");
    }

    public void testCallbacksAreNotHighlighted() {
        myFixture.enableInspections(new MissingModulePHPInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");
        myFixture.addFileToProject("foo_bar/Resources/Public/JavaScript/Baz.js", "");

        myFixture.testHighlighting("js_module_reference_invalid_callback.php");
    }

    public void testConcatenatedModuleNamesAreNotAnalyzed() {
        myFixture.enableInspections(new MissingModulePHPInspection());

        myFixture.copyFileToProject("classes.php");
        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");

        myFixture.testHighlighting("js_module_reference_concat.php");
    }
}
