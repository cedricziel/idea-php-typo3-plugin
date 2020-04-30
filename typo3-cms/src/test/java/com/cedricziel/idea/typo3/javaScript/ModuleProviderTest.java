package com.cedricziel.idea.typo3.javaScript;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.lang.javascript.frameworks.modules.JSResolvableModuleReference;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;

import java.util.ArrayList;
import java.util.List;

public class ModuleProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/javaScript";
    }

    public void testJavaScriptReferenceIsProvided() {
        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");
        myFixture.copyFileToProject("define_reference.js", "foo_bar/Resources/Public/JavaScript/MyMagicModule.js");
        VirtualFile usageFile = myFixture.copyFileToProject("use_reference.js", "foo_bar/Resources/Public/JavaScript/MyOtherModule.js");

        myFixture.configureFromExistingVirtualFile(usageFile);

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();

        PsiReference[] references = element.getReferences();

        List<PsiElement> collected = new ArrayList<>();
        for (PsiReference reference : references) {
            if (reference instanceof JSResolvableModuleReference && reference.resolve() != null) {
                JSResolvableModuleReference reference1 = (JSResolvableModuleReference) reference;
                collected.add(reference1.resolve());
            }
        }

        assertSize(1, collected);
        assertEquals("MyMagicModule.js", ((PsiFile) collected.get(0)).getName());
    }

    public void testModuleNameCompletionIsProvided() {
        myFixture.addFileToProject("foo_bar/ext_emconf.php", "");
        myFixture.copyFileToProject("define_reference.js", "foo_bar/Resources/Public/JavaScript/MyMagicModule.js");
        myFixture.copyFileToProject("define_reference.js", "foo_bar/Resources/Public/JavaScript/MyOtherModule.js");
        myFixture.configureByFile("use_reference_completion.js");

        myFixture.testCompletionVariants("use_reference_completion.js", "TYPO3/CMS/FooBar/MyMagicModule", "TYPO3/CMS/FooBar/MyOtherModule");
    }
}
