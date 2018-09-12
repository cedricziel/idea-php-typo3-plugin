package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.AbstractFluidTest;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

public class PhpGlobalsNamespaceProviderTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/namespaces";
    }

    public void testExtLocalconfNamespacesAreLoaded() {
        myFixture.copyFileToProject("ext_localconf.php");

        PsiFile psiFile = myFixture.configureByText("foo.fluid", "{fo<caret>o}");

        int offset = myFixture.getEditor().getCaretModel().getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset).getParent();

        List<FluidNamespace> namespaces = FluidUtil.getFluidNamespaces(elementAtCaret);

        assertContainsNamespace(namespaces, "v", "FluidTYPO3/Vhs/ViewHelpers");
        assertContainsNamespace(namespaces, "formvh", "TYPO3/CMS/Form/ViewHelpers");
    }
}
