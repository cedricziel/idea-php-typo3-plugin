package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.jetbrains.php.lang.inspections.deadcode.PhpUnusedDeclarationInspection;

public class UnusedActionControllerMethodInspectionSuppressorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testInspectionIsSuppressedInActionControllerActions() {
        myFixture.copyFileToProject("classes.php");
        myFixture.enableInspections(new PhpUnusedDeclarationInspection());

        myFixture.testHighlighting("action_controller.php");
    }
}
