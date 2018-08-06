package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.List;

public class HtmlNSNamespaceProviderTest extends LightCodeInsightFixtureTestCase {
    public void testHtmlNamespacesCanBeProvided() {
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

        List<FluidNamespace> namespaces = new ArrayList<>();
        for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
            namespaces.addAll(extension.provideForElement(elementAtCaret));
        }

        assertContainsNamespace(namespaces, "f", "TYPO3/CMS/Fluid/ViewHelpers");
        assertContainsNamespace(namespaces, "formvh", "TYPO3/CMS/Form/ViewHelpers");
    }

    private void assertContainsNamespace(List<FluidNamespace> namespaces, String prefix, String namespace) {
        for (FluidNamespace fluidNamespace : namespaces) {
            if (fluidNamespace.prefix.equals(prefix) && fluidNamespace.namespace.equals(namespace)) {
                return;
            }
        }

        fail(String.format("Expected %s bound to %s, but didnt find it.", namespace, prefix));
    }
}
