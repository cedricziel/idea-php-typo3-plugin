package com.cedricziel.idea.typo3.codeInspection.daemon;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.daemon.impl.analysis.XmlUnusedNamespaceInspection;

public class XmlNamespaceImplicitUsageProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection/daemon";
    }

    public void testOptimizeImportsDoesNotRemoveImports() {
        myFixture.enableInspections(new XmlUnusedNamespaceInspection());

        myFixture.configureByFile("unused_namespace.html");
        myFixture.performEditorAction("OptimizeImports");
        myFixture.checkResultByFile("unused_namespace.html");
    }
}
