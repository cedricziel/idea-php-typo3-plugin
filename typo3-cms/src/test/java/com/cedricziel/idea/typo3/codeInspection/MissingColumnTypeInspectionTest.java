package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class MissingColumnTypeInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testMissingColumnTypeIsMarked() {
        myFixture.enableInspections(MissingColumnTypeInspection.class);

        VirtualFile virtualFile = myFixture.copyFileToProject("MissingColumnTypeInspectionIsMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }

    public void testMissingColumnTypeIsNotMarkedWhenPluginDisabled() {
        disablePlugin();

        myFixture.enableInspections(MissingColumnTypeInspection.class);

        VirtualFile virtualFile = myFixture.copyFileToProject("MissingColumnTypeInspectionIsNotMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }
}
