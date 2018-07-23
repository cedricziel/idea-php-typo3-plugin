package com.cedricziel.idea.fluid.viewHelpers.xml;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl;
import com.intellij.psi.impl.source.xml.XmlDescriptorUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ViewHelperXmlElementDescriptor implements XmlElementDescriptor {
    private final String tagName;
    private final XmlTag tag;
    private final ViewHelper viewHelper;

    public ViewHelperXmlElementDescriptor(String tagName, XmlTag tag, ViewHelper viewHelper) {
        this.tagName = tagName;
        this.tag = tag;
        this.viewHelper = viewHelper;
    }

    @Override
    public PsiElement getDeclaration() {
        return null;
    }

    @Override
    public String getName(PsiElement context) {
        return getName();
    }

    @Override
    public String getName() {
        return tag.getName();
    }

    @Override
    public void init(PsiElement element) {

    }

    @NotNull
    @Override
    public Object[] getDependences() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    @Override
    public String getQualifiedName() {
        return getName();
    }

    @Override
    public String getDefaultName() {
        return getName();
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        return XmlDescriptorUtil.getElementsDescriptors(context);
    }

    @Nullable
    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        return XmlDescriptorUtil.getElementDescriptor(childTag, contextTag);
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
        Collection<XmlAttributeDescriptor> attributeDescriptors = new ArrayList<>();

        viewHelper.arguments.forEach((s, viewHelperArgument) -> {
            attributeDescriptors.add(new ViewHelperArgumentDescriptor(viewHelper, viewHelperArgument));
        });

        final XmlAttributeDescriptor[] commonAttributes = HtmlNSDescriptorImpl.getCommonAttributeDescriptors(context);

        return ArrayUtil.mergeArrays(attributeDescriptors.toArray(new XmlAttributeDescriptor[0]), commonAttributes);
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, @Nullable XmlTag context) {
        if (!viewHelper.arguments.containsKey(attributeName)) {
            return null;
        }

        return new ViewHelperArgumentDescriptor(viewHelper, viewHelper.arguments.get(attributeName));
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        return getAttributeDescriptor(attribute.getName(), attribute.getParent());
    }

    @Nullable
    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return null;
    }

    @Nullable
    @Override
    public XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public int getContentType() {
        return CONTENT_TYPE_ANY;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return null;
    }
}
