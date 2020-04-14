package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.intention.IntentionAction;

import java.util.List;

public class LegacyClassesForIDEInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testLegacyClassUsagesAreNotMarkedWhenPluginDisabled() {
        disablePlugin();

        myFixture.enableInspections(new LegacyClassesForIDEInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("LegacyClassesForIDE_classes.php");
        myFixture.copyFileToProject("LegacyClassesForIDE_index.php", "foo/Migrations/Code/LegacyClassesForIde.php");

        myFixture.configureByFile("LegacyClassesForIDE_highlight_disabled.php");

        myFixture.checkHighlighting();
    }

    public void testLegacyClassUsagesAreMarkedAndCanBeFixed() {
        myFixture.enableInspections(new LegacyClassesForIDEInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("LegacyClassesForIDE_classes.php");
        myFixture.copyFileToProject("LegacyClassesForIDE_index.php", "foo/Migrations/Code/LegacyClassesForIde.php");

        myFixture.configureByFile("LegacyClassesForIDE_highlight.php");

        myFixture.checkHighlighting();

        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        for (IntentionAction action : allQuickFixes) {
            myFixture.launchAction(action);
        }
        myFixture.checkResultByFile("LegacyClassesForIDE_highlight_after.php", true);
    }
}
