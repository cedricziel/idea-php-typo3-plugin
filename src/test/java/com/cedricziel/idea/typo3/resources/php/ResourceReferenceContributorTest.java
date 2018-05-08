package com.cedricziel.idea.typo3.resources.php;

import com.cedricziel.idea.typo3.resources.ResourceLookupElement;
import com.cedricziel.idea.typo3.resources.ResourceReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.PhpFileType;

public class ResourceReferenceContributorTest extends LightCodeInsightFixtureTestCase {
    public void testResourceReferencesAreCreated() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "'EXT:foo/ext_emconf.php<caret>';");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference ref : references) {
            if (ref instanceof ResourceReference) {
                return;
            }
        }

        fail("No resource reference.");
    }

    public void testResourceReferenceHasVariants() {
        myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.addFileToProject("foo/bar.txt", "");

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "'EXT:foo/ext_emconf.php<caret>';");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        ResourceReference target = null;
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference ref : references) {
            if (ref instanceof ResourceReference) {
                target = (ResourceReference) ref;
            }
        }

        if (target != null) {
            for (Object o : target.getVariants()) {
                if (o instanceof ResourceLookupElement && ((ResourceLookupElement) o).getLookupString().contains("EXT:foo/bar.txt")) {
                    return;
                }
            }
        }

        fail("No resource reference.");
    }

    public void testResourceReferenceResolves() {
        PsiFile psiFile = myFixture.addFileToProject("foo/ext_emconf.php", "");
        myFixture.addFileToProject("foo/bar.txt", "");

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "'EXT:foo/ext_emconf.php<caret>';");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        ResourceReference target = null;
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference ref : references) {
            if (ref instanceof ResourceReference) {
                target = (ResourceReference) ref;
            }
        }

        if (target != null) {
            for (ResolveResult o : target.multiResolve(true)) {
                if (o.getElement().isEquivalentTo(psiFile)) {
                    return;
                }
            }
        }

        fail("No resource reference.");
    }
}
