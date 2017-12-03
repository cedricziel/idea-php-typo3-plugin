package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.index.IconIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;


public class IconReferenceProviderTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/icons";
    }

    public void testIconsAreDetectedForv7() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry7.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }

    public void testIconsAreDetectedForv8() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry8.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-edit-cut-release");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }

    public void testIconsAreDetectedForv9() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry9.php");

        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("actions-wizard-link");
        IconIndex.getAllAvailableIcons(myFixture.getProject()).contains("flags-pg");
    }

    public void testIconReferenceIsCreatedOnGetIcon() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.configureByFile("IconRegistry9.php");
        myFixture.configureByFile("icon_provider_test.php");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference ref : references) {
            if (ref instanceof IconReference) {
                return;
            }
        }

        fail("No icon reference found");
    }
}
