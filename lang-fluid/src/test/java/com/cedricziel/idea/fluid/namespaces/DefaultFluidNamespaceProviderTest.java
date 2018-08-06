package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.AbstractFluidTest;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

public class DefaultFluidNamespaceProviderTest extends AbstractFluidTest {
    public void testDefaultNamespaceIsAlwaysAvailable() {
        PsiFile psiFile = myFixture.configureByText("foo.fluid", "<html xmlns:f=\"http://typo3.org/ns/TYPO3/CMS/Fluid/ViewHelpers\" xmlns:formvh=\"http://typo3.org/ns/TYPO3/CMS/Form/ViewHelpers\" data-namespace-typo3-fluid=\"true\">\n" +
            "<formvh:renderRenderable renderable=\"{element}\">\n" +
            "\t<f:render partial=\"Field/Field\" arguments=\"{element: element}\" contentAs=\"elementContent\">\n" +
            "\t\t<f:form.textfield\n" +
            "\t\t\t\ttype=\"url\"\n" +
            "\t\t\t\tproperty=\"{element.identifier<caret>}\"\n" +
            "\t\t\t\tid=\"{element.uniqueIdentifier}\"\n" +
            "\t\t\t\tclass=\"{element.properties.elementClassAttribute} form-control\"\n" +
            "\t\t\t\terrorClass=\"{element.properties.elementErrorClassAttribute}\"\n" +
            "\t\t\t\tadditionalAttributes=\"{formvh:translateElementProperty(element: element, property: 'fluidAdditionalAttributes')}\"\n" +
            "\t\t/>\n" +
            "\t</f:render>\n" +
            "</formvh:renderRenderable>\n" +
            "</html>");

        int offset = myFixture.getEditor().getCaretModel().getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset).getParent();

        List<FluidNamespace> namespaces = FluidUtil.getFluidNamespaces(elementAtCaret);

        assertContainsNamespace(namespaces, "f", "TYPO3/Fluid/ViewHelpers");
    }
}
