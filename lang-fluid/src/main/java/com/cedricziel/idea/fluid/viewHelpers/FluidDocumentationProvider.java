package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidDocumentationProvider extends AbstractDocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (!(originalElement instanceof XmlTag)) {
            return null;
        }

        XmlTag xmlTag = (XmlTag) originalElement;

        Map<String, ViewHelper> allViewHelpersInContextByName = ViewHelperUtil.findAllViewHelpersInContextByName(element.getProject(), originalElement);
        boolean b = allViewHelpersInContextByName.containsKey(xmlTag.getName());
        if (!b) {
            return null;
        }

        String documentation = allViewHelpersInContextByName.get(xmlTag.getName()).getDocumentation();
        return StringUtils.substring(documentation, 0, 100);
    }
}
