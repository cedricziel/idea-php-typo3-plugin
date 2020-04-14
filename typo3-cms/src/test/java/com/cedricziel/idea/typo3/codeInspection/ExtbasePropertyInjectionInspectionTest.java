package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public class ExtbasePropertyInjectionInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testExtbasePropertyInjectionIsMarked() {
        myFixture.enableInspections(new ExtbasePropertyInjectionInspection());

        VirtualFile virtualFile = myFixture.copyFileToProject("ExtbasePropertyInjectionInspectionIsMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }

    public void testExtbasePropertyInjectionIsMarkedAndCanBeFixed() {
        myFixture.enableInspections(new ExtbasePropertyInjectionInspection());
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes("ExtbasePropertyInjectionInspectionIsMarkedAndCanBeFixed.php");
        for (IntentionAction action : allQuickFixes) {
            myFixture.launchAction(action);
        }
        myFixture.checkResultByFile("ExtbasePropertyInjectionInspectionIsMarkedAndCanBeFixed_after.php", true);
    }

    public void testExtbasePropertyInjectionIsNotMarkedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.enableInspections(new ExtbasePropertyInjectionInspection());

        VirtualFile virtualFile = myFixture.copyFileToProject("ExtbasePropertyInjectionInspectionIsNotMarked.php", "MyClass.php");
        myFixture.configureFromExistingVirtualFile(virtualFile);

        myFixture.checkHighlighting();
    }
}
