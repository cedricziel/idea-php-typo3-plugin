package com.cedricziel.idea.typo3.translation;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlTag;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.PhpFileType;

public class TranslationReferenceTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> {
            FileTypeManager.getInstance().associateExtension(XmlFileType.INSTANCE, "xlf");
        });

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");

        myFixture.copyFileToProject("sample.xlf", "typo3conf/ext/foo/sample.xlf");
        myFixture.copyFileToProject("de.sample.xlf", "typo3conf/ext/foo/de.sample.xlf");
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/translation";
    }

    public void testReferenceCanResolveDefinition() {
        PsiFile file = myFixture.configureByText(PhpFileType.INSTANCE, "<?php \n" +
                "\"LLL:EXT:foo/sample.xlf:sys_<caret>language.language_isocode.ab\";");

        PsiElement elementAtCaret = file.findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = elementAtCaret.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof TranslationReference) {
                ResolveResult[] resolveResults = ((TranslationReference) reference).multiResolve(false);
                for (ResolveResult resolveResult : resolveResults) {
                    assertInstanceOf(resolveResult.getElement(), XmlTag.class);

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
