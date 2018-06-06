package com.cedricziel.idea.typo3.translation;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.jetbrains.php.lang.PhpFileType;

public class TranslationReferenceTest extends AbtractTranslationTest {
    public void testReferenceCanResolveDefinition() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";");

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof TranslationReference) {
                ResolveResult[] resolveResults = ((TranslationReference) reference).multiResolve(false);
                for (ResolveResult resolveResult : resolveResults) {
                    assertInstanceOf(resolveResult.getElement(), XmlAttributeValue.class);

                    return;
                }
            }
        }

        fail("No TranslationReference found");
    }

    public void testReferenceCanResolveVariants() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";");

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof TranslationReference) {
                Object[] variants = reference.getVariants();
                for (Object variant : variants) {
                    if (variant instanceof LookupElement) {
                        String lookupString = ((LookupElement) variant).getLookupString();
                        assertEquals("LLL:EXT:foo/sample.xlf:sys_language.language_isocode.ab", lookupString);
                        return;
                    }
                }

            }
        }

        fail("No TranslationReference found");
    }
}
