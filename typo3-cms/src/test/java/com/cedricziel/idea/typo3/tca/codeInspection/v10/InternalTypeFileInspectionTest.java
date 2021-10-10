package com.cedricziel.idea.typo3.tca.codeInspection.v10;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.openapi.vfs.VirtualFile;

public class InternalTypeFileInspectionTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tca/codeInspection/v10";
    }

    public void testInvalidInternalTypeIsNotDisplayedWhenPluginIsDisabled() {
        disablePlugin();

        myFixture.enableInspections(new InternalTypeFileInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_internal_type_disabled.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }

    public void testInvalidInternalTypeIsNotDisplayedOnv8() {
        myFixture.enableInspections(new InternalTypeFileInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_internal_type_v8.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }

    public void testInvalidInternalTypeIsDisplayedOnv9() {
        myFixture.enableInspections(new InternalTypeFileInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_internal_type_v9.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }

    public void testInvalidInternalTypeIsHighlightedOnv10() {
        myFixture.enableInspections(new InternalTypeFileInspection());

        myFixture.addFileToProject("foo/ext_emconf.php", "");

        VirtualFile virtualFile = myFixture.copyFileToProject("invalid_internal_type_v10.php", "foo/Configuration/TCA/tx_news_domain_model_link.php");

        myFixture.configureFromExistingVirtualFile(virtualFile);
        myFixture.checkHighlighting();
    }
}
