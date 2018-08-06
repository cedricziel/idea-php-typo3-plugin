package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementWalkingVisitor;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class HtmlNSNamespaceProvider implements NamespaceProvider {
    private static final String fluidNamespaceURIPrefix = "http://typo3.org/ns/";

    private final Key<CachedValue> HTML_NS_KEY = new Key<>("FLUID_HTML_NS");

    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        return doProvide(element);
    }

    private synchronized Collection<FluidNamespace> doProvide(@NotNull PsiElement element) {
        FileViewProvider viewProvider = element.getContainingFile().getViewProvider();
        if (!viewProvider.getLanguages().contains(HTMLLanguage.INSTANCE)) {
            return ContainerUtil.emptyList();
        }

        PsiFile htmlFile = viewProvider.getPsi(HTMLLanguage.INSTANCE);
        CachedValue userData = htmlFile.getUserData(HTML_NS_KEY);
        if (userData != null) {
            return (Collection<FluidNamespace>) userData.getValue();
        }

        CachedValue<Collection<FluidNamespace>> cachedValue = CachedValuesManager.getManager(element.getProject()).createCachedValue(() -> {
            HtmlNSVisitor visitor = new HtmlNSVisitor();
            htmlFile.accept(visitor);

            return CachedValueProvider.Result.createSingleDependency(visitor.namespaces, htmlFile);
        }, false);

        htmlFile.putUserData(HTML_NS_KEY, cachedValue);

        return cachedValue.getValue();
    }

    private static class HtmlNSVisitor extends XmlRecursiveElementWalkingVisitor {
        Collection<FluidNamespace> namespaces = new ArrayList<>();

        @Override
        public void visitXmlAttribute(XmlAttribute attribute) {
            if (attribute.getName().startsWith("xmlns:") && attribute.getValue() != null) {
                if (attribute.getValue().startsWith(fluidNamespaceURIPrefix)) {
                    namespaces.add(new FluidNamespace(attribute.getLocalName(), attribute.getValue().substring(fluidNamespaceURIPrefix.length())));
                }
            }

            super.visitXmlAttribute(attribute);
        }
    }
}
