package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class MissingTableInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testMissingRenderTypeIsMarked() {
        myFixture.enableInspections(new MissingTableInspection());

        VirtualFile virtualFile = myFixture.copyFileToProject("MissingTableInspectionIsMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }

    public void testMissingRenderTypeIsNotMarkedWhenPluginDisabled() {
        disablePlugin();

        myFixture.enableInspections(new MissingTableInspection());

        VirtualFile virtualFile = myFixture.copyFileToProject("MissingTableInspectionIsNotMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }

    public void testAllowedKeyCanContainCSVOfTables() {
        myFixture.enableInspections(new MissingTableInspection());

        VirtualFile virtualFile = myFixture.copyFileToProject("MissingTableInspectionIsNotMarkedForAllowed.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }
}
