package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;


public class IconReferenceProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/icons";
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

    public void skipTestIconReferenceIsCreatedOnGetIconFromGeneralUtility() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.copyFileToProject("IconRegistry9.php");
        myFixture.configureByFiles("general_utility_icon_provider_test.php");

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
