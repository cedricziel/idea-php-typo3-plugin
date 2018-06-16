package com.cedricziel.idea.typo3.userFunc;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;

public class UserFuncReferenceContributorTest extends AbstractTestCase {

    public void testCanCreateUserFuncReferencesOnXmlStrings() {
        PsiFile file = myFixture.configureByText(
                "foo.xml",
                "<userFunc>coun<caret>t</userFunc>"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset());
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference: references) {
            if (reference instanceof UserFuncReference) {
                return;
            }
        }

        fail("No UserFuncReference found");
    }

    public void testCanCreateItemsProcFuncReferencesOnXmlStrings() {
        PsiFile file = myFixture.configureByText(
                "foo.xml",
                "<itemsProcFunc>coun<caret>t</itemsProcFunc>"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset());
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference: references) {
            if (reference instanceof UserFuncReference) {
                return;
            }
        }

        fail("No UserFuncReference found");
    }

    public void testCanCreateUserFuncReferencesOnPHPArrays() {
        PsiFile file = myFixture.configureByText(
                "foo.php",
                "<?php \n" +
                        "$foo = ['userFunc' => 'coun<caret>t'];"
        );

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference: references) {
            if (reference instanceof UserFuncReference) {
                return;
            }
        }

        fail("No UserFuncReference found");
    }
}
