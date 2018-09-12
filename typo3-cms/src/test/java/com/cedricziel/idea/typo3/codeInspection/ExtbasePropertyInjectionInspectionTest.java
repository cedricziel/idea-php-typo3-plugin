package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class ExtbasePropertyInjectionInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testExtbasePropertyInjectionIsMarked() {
        myFixture.enableInspections(ExtbasePropertyInjectionInspection.class);

        VirtualFile virtualFile = myFixture.copyFileToProject("ExtbasePropertyInjectionInspectionIsMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }

    public void testExtbasePropertyInjectionIsNotMarkedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.enableInspections(ExtbasePropertyInjectionInspection.class);

        VirtualFile virtualFile = myFixture.copyFileToProject("ExtbasePropertyInjectionInspectionIsNotMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }
}
