package com.cedricziel.idea.typo3.tca.codeInspection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class InvalidQuantityInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tca/codeInspection";
    }

    public void testInvalidQuantitiesAreNotHighlightedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.enableInspections(InvalidQuantityInspection.class);

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_quantity_disabled.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }

    public void testInvalidQuantitiesAreHighlighted() {
        myFixture.enableInspections(InvalidQuantityInspection.class);

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_quantity.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }
}
