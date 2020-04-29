package com.cedricziel.idea.typo3.javaScript;

import com.cedricziel.idea.typo3.util.JavaScriptUtil;
import com.intellij.lang.javascript.frameworks.modules.JSResolvableModuleReferenceContributor;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.lang.javascript.psi.util.JSUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModuleProvider extends JSResolvableModuleReferenceContributor {
    @NotNull
    @Override
    protected ResolveResult[] resolveElement(@NotNull PsiElement psiElement, @NotNull String s) {
        if (s.startsWith(JavaScriptUtil.MODULE_PREFIX)) {
            return ResolveResult.EMPTY_ARRAY;
        }

        List<PsiFile> moduleFilesFor = JavaScriptUtil.findModuleFilesFor(psiElement.getProject(), s);

        if (moduleFilesFor.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY;
        }

        return JSResolveResult.toResolveResults(moduleFilesFor);
    }

    @Override
    public int getDefaultWeight() {
        return 25;
    }
}
