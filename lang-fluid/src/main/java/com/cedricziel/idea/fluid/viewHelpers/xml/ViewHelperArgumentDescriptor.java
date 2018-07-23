package com.cedricziel.idea.fluid.viewHelpers.xml;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.xml.NamespaceAwareXmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewHelperArgumentDescriptor implements XmlAttributeDescriptor, NamespaceAwareXmlAttributeDescriptor {

    private final ViewHelper viewHelper;
    private final ViewHelperArgument viewHelperArgument;

    public ViewHelperArgumentDescriptor(ViewHelper viewHelper, ViewHelperArgument viewHelperArgument) {
        this.viewHelper = viewHelper;
        this.viewHelperArgument = viewHelperArgument;
    }

    @Override
    public boolean isRequired() {
        return viewHelperArgument.required;
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean hasIdType() {
        return false;
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public boolean isEnumerated() {
        return false;
    }

    @Nullable
    @Override
    public String[] getEnumeratedValues() {
        return new String[0];
    }

    @Nullable
    @Override
    public String validateValue(XmlElement context, String value) {
        return null;
    }

    @Override
    public PsiElement getDeclaration() {
        return null;
    }

    @Override
    public String getName(PsiElement context) {
        return viewHelperArgument.name;
    }

    @Override
    public String getName() {
        return viewHelperArgument.name;
    }

    @Override
    public void init(PsiElement element) {

    }

    @NotNull
    @Override
    public Object[] getDependences() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    @Nullable
    @Override
    public String getNamespace(@NotNull XmlTag context) {
        return null;
    }
}
