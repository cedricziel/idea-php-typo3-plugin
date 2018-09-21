package com.cedricziel.idea.typo3.translation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.PhpFileType;

public class TranslationReferenceProviderTest extends AbstractTranslationTest {

    public void testCanCreateTranslationReferencesOnPhpStrings() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";");

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof TranslationReference) {
                return;
            }
        }

        fail("No TranslationReference found");
    }
}
