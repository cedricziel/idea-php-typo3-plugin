package com.cedricziel.idea.typo3.javaScript;

import com.cedricziel.idea.typo3.util.ResourceUtil;
import com.intellij.lang.javascript.frameworks.amd.JSModuleReference;
import com.intellij.lang.javascript.frameworks.modules.JSModuleFileReferenceSet;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.resolve.JSModuleReferenceContributor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TYPO3RequireJSModuleReferenceContributor implements JSModuleReferenceContributor {
    @NotNull
    @Override
    public PsiReference[] getCommonJSModuleReferences(@NotNull String s, @NotNull PsiElement psiElement, int i, @Nullable PsiReferenceProvider psiReferenceProvider) {
        FileReferenceSet fileReferenceSet = FileReferenceSet.createSet(psiElement, true, false, true);
        JSModuleReference ref = new JSModuleReference("foo", 0, new TextRange(0, psiElement.getTextLength()), fileReferenceSet, null, true);

        return new PsiReference[]{ref};
    }

    @NotNull
    @Override
    public PsiReference[] getAllReferences(@NotNull String s, @NotNull PsiElement psiElement, int i, @Nullable PsiReferenceProvider psiReferenceProvider) {
        if (!s.startsWith("TYPO3/CMS/")) {
            return PsiReference.EMPTY_ARRAY;
        }

        String resourceName = JavaScriptUtil.convertModuleNameToResourceIdentifier(s);
        PsiElement[] resourceDefintionElements = ResourceUtil.getResourceDefinitionElements(psiElement.getProject(), resourceName);

        JSModuleFileReferenceSet jsModuleFileReferenceSet = new JSModuleFileReferenceSet(resourceDefintionElements[0], psiElement, 0, psiReferenceProvider, "");
        jsModuleFileReferenceSet.fil

        return jsModuleFileReferenceSet.getAllReferences();
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement psiElement) {

        return psiElement instanceof JSLiteralExpression;
    }
}
