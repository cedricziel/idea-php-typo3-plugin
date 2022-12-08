package com.cedricziel.idea.typo3.javaScript;

import com.cedricziel.idea.typo3.util.JavaScriptUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.javascript.completion.JSLookupPriority;
import com.intellij.lang.javascript.completion.JSLookupUtilImpl;
import com.intellij.lang.javascript.frameworks.modules.JSResolvableModuleReferenceContributor;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModuleProvider extends JSResolvableModuleReferenceContributor {
    @NotNull
    @Override
    protected ResolveResult @NotNull [] resolveElement(@NotNull PsiElement psiElement, @NotNull String s) {
        if (!s.startsWith(JavaScriptUtil.MODULE_PREFIX)) {
            return ResolveResult.EMPTY_ARRAY;
        }

        List<PsiFile> moduleFilesFor = JavaScriptUtil.findModuleFilesFor(psiElement.getProject(), s);

        if (moduleFilesFor.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY;
        }

        return JSResolveResult.toResolveResults(moduleFilesFor);
    }

    @NotNull
    public Collection<LookupElement> getLookupElements(@NotNull String unquotedEscapedText, @NotNull PsiElement host) {
        List<LookupElement> result = new ArrayList<>();

        JavaScriptUtil.getModuleMap(host.getProject()).forEach((name, file) -> {
            for (PsiFile psiFile : file) {
                LookupElement lookupItem = JSLookupUtilImpl.createPrioritizedLookupItem(psiFile, name, JSLookupPriority.MAX_PRIORITY);
                if (lookupItem != null) {
                    result.add(lookupItem);
                }
            }
        });

        return result;
    }

    @Override
    public int getDefaultWeight() {
        return 25;
    }
}
