package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.intention.IntentionAction;

import java.util.List;

public class FluidNamespaceWithoutDataAttributeInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testMissingDataAttributeIsHighlighted() {
        myFixture.enableInspections(new FluidNamespaceWithoutDataAttributeInspection());

        myFixture.testHighlighting("fluid_data_attribute_missing.html");
    }

    public void testApplyingQuickFixAddsTheAttribute() {
        myFixture.enableInspections(new FluidNamespaceWithoutDataAttributeInspection());

        final List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes("fluid_data_attribute_missing_fix.html");
        for (IntentionAction allQuickFix : allQuickFixes) {
            if (allQuickFix.getText().equals("Add fluid data attribute")) {
                myFixture.launchAction(allQuickFix);
            }
        }

        myFixture.checkResultByFile("fluid_data_attribute_missing_fix_after.html");
    }
}
