package com.cedricziel.idea.fluid.tagMode;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class UnboundNamespaceSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!toolId.equals("XmlUnboundNsPrefix")) {
            return false;
        }

        if (!element.getContainingFile().getViewProvider().getLanguages().contains(FluidLanguage.INSTANCE) || element.getLanguage() == FluidLanguage.INSTANCE) {
            return false;
        }

        if (!(element instanceof XmlTokenImpl) || !((XmlTokenImpl) element).getElementType().equals(XmlElementType.XML_NAME)) {
            return false;
        }

        String prefix = ((XmlTag) element.getParent()).getNamespacePrefix();
        for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
            Collection<FluidNamespace> fluidNamespaces = extension.provideForElement(element);
            for (FluidNamespace fluidNamespace : fluidNamespaces) {
                if (fluidNamespace.prefix.equals(prefix)) {
                    return true;
                }
            }
        }

        return false;
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return new SuppressQuickFix[0];
    }
}
