package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.daemon.impl.analysis.XmlUnusedNamespaceInspection;

public class XmlUnusedNamespaceDeclarationSuppressorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInspection";
    }

    public void testUnusedNamespacesInspectionIsSuppressed() {
        myFixture.enableInspections(new XmlUnusedNamespaceInspection());

        myFixture.testHighlighting("unused_namespace.html");
    }
}
