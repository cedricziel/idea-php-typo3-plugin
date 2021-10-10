package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class MissingRenderTypeInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testMissingRenderTypeIsMarked() {
        myFixture.enableInspections(new MissingRenderTypeInspection());

        myFixture.testHighlighting("MissingRenderTypeInspectionIsMarked.php");
    }

    public void testMissingRenderTypeIsNotMarkedWhenPluginDisabled() {
        disablePlugin();

        myFixture.enableInspections(new MissingRenderTypeInspection());

        myFixture.testHighlighting("MissingRenderTypeInspectionIsNotMarked.php");
    }
}
