package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class MissingColumnTypeInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testMissingColumnTypeIsMarked() {
        myFixture.enableInspections(new MissingColumnTypeInspection());

        myFixture.testHighlighting("MissingColumnTypeInspectionIsMarked.php");
    }

    public void testMissingColumnTypeIsNotMarkedWhenPluginDisabled() {
        disablePlugin();

        myFixture.enableInspections(new MissingColumnTypeInspection());

        myFixture.testHighlighting("MissingColumnTypeInspectionIsNotMarked.php");
    }

    public void testColumnTypeSlugIsNotAvailableForV8() {
        myFixture.enableInspections(new MissingColumnTypeInspection());

        myFixture.testHighlighting("MissingColumnTypeSlugIsMarkedOnV8.php");
    }

    public void testColumnTypeSlugIsAvailableForV9() {
        myFixture.enableInspections(new MissingColumnTypeInspection());

        myFixture.testHighlighting("MissingColumnTypeSlugIsNotMarkedOnV9.php");
    }
}
