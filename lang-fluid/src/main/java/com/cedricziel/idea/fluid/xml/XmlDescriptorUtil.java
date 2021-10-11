package com.cedricziel.idea.fluid.xml;

import com.intellij.html.impl.DelegatingRelaxedHtmlElementDescriptor;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlNSDescriptorEx;
import org.jetbrains.annotations.NotNull;

import static com.intellij.xml.XmlElementDescriptor.EMPTY_ARRAY;

/**
 * Backported from 2018.2
 */
public class XmlDescriptorUtil {
    public static XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        XmlDocumentImpl xmlDocument = PsiTreeUtil.getParentOfType(context, XmlDocumentImpl.class);
        if (xmlDocument == null) return EMPTY_ARRAY;
        return ContainerUtil.map2Array(xmlDocument.getRootTagNSDescriptor().getRootElementsDescriptors(xmlDocument),
            XmlElementDescriptor.class, XmlDescriptorUtil::wrapInDelegating);
    }

    public static XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        final XmlDocument document = PsiTreeUtil.getParentOfType(contextTag, XmlDocument.class);
        if (document == null) {
            return null;
        }
        final XmlNSDescriptor nsDescriptor = document.getDefaultNSDescriptor(childTag.getNamespace(), true);
        if (nsDescriptor instanceof XmlNSDescriptorEx) {
            XmlElementDescriptor descriptor = ((XmlNSDescriptorEx) nsDescriptor).getElementDescriptor(childTag.getLocalName(), childTag.getNamespace());
            return descriptor != null ? wrapInDelegating(descriptor) : null;
        }
        return null;
    }

    @NotNull
    public static DelegatingRelaxedHtmlElementDescriptor wrapInDelegating(XmlElementDescriptor descriptor) {
        return descriptor instanceof DelegatingRelaxedHtmlElementDescriptor ? (DelegatingRelaxedHtmlElementDescriptor)descriptor :
            new DelegatingRelaxedHtmlElementDescriptor(descriptor);
    }
}
