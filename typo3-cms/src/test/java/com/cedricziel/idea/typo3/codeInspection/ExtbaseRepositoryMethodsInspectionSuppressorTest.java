package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.jetbrains.php.lang.inspections.PhpUndefinedMethodInspection;

public class ExtbaseRepositoryMethodsInspectionSuppressorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testExtbaseRepositoryFinderMethodsAreNotMarkedMissing() {
        myFixture.copyFileToProject("classes.php");

        myFixture.enableInspections(new PhpUndefinedMethodInspection());

        myFixture.testHighlighting("extbase_repository_finder_methods_are_not_marked_missing.php");
    }
}
