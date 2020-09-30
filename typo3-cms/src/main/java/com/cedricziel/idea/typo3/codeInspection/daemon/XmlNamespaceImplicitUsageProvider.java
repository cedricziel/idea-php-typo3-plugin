package com.cedricziel.idea.typo3.codeInspection.daemon;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;

public class XmlNamespaceImplicitUsageProvider implements ImplicitUsageProvider {
    /**
     * @param element The element
     * @return true if element should not be reported as unused
     */
    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        if (!(element.getLanguage() instanceof XMLLanguage)) {
            return false;
        }

        if (!(element instanceof XmlAttribute)) {
            return false;
        }

        if (!((XmlAttribute) element).isNamespaceDeclaration()) {
            return false;
        }

        String namespace = ((XmlAttribute) element).getValue();

        return namespace != null && namespace.contains("typo3.org/ns");
    }

    /**
     * @param element The element
     * @return true if element should not be reported as "assigned but not used"
     */
    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        return false;
    }

    /**
     * @param element The element
     * @return true if element should not be reported as "referenced but never assigned"
     */
    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return false;
    }
}
