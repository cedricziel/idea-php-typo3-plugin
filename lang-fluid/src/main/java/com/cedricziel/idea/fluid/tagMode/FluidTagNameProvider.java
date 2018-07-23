package com.cedricziel.idea.fluid.tagMode;


import com.cedricziel.idea.fluid.lang.FluidFileType;
import com.cedricziel.idea.fluid.viewHelpers.ViewHelperUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlFileNSInfoProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class FluidTagNameProvider implements XmlElementDescriptorProvider, XmlAttributeDescriptorsProvider, XmlTagNameProvider, XmlFileNSInfoProvider {
    @Override
    public void addTagNameVariants(List<LookupElement> elements, @NotNull XmlTag tag, String prefix) {
        Collection<LookupElement> collection = ViewHelperUtil.tagNameVariantsLookupElementsForCurrentPosition(tag.getProject(), tag, prefix);
        elements.addAll(collection);
    }

    @Nullable
    @Override
    public XmlElementDescriptor getDescriptor(XmlTag tag) {
        if (tag == null) {
            return null;
        }

        return ViewHelperUtil.xmlElementDescriptorForCurrentTag(tag.getProject(), tag);
    }

    @Nullable
    @Override
    public String[][] getDefaultNamespaces(@NotNull XmlFile file) {
        return (file.getFileType() == FluidFileType.INSTANCE || file.getFileType() == HtmlFileType.INSTANCE|| file.getFileType() == XmlFileType.INSTANCE) ? new String[][]{
            {"f", "http://typo3.org/ns/TYPO3/Fluid/ViewHelpers"},
            {"", "http://www.w3.org/1999/html"}
        } : null;
    }

    @Override
    public boolean overrideNamespaceFromDocType(@NotNull XmlFile file) {
        return false;
    }

    @Override
    public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag context) {
        XmlElementDescriptor descriptor = getDescriptor(context);
        if (descriptor == null) {
            return XmlAttributeDescriptor.EMPTY;
        }

        return descriptor.getAttributesDescriptors(context);
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, XmlTag context) {
        XmlElementDescriptor descriptor = getDescriptor(context);
        if (descriptor == null) {
            return null;
        }

        return descriptor.getAttributeDescriptor(attributeName, context);
    }
}
