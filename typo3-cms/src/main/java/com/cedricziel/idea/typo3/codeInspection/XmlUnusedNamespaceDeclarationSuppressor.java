package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlUnusedNamespaceDeclarationSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!(element.getLanguage() instanceof XMLLanguage)) {
            return false;
        }

        if (!toolId.equals("XmlUnusedNamespaceDeclaration")) {
            return false;
        }

        if (!(element instanceof XmlAttribute)) {
            return false;
        }

        String namespace = ((XmlAttribute) element).getValue();

        return namespace != null && namespace.contains("typo3.org/ns");
    }

    @NotNull
    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
