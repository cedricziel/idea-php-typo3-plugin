package com.cedricziel.idea.typo3.fluid;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ExtLocalconfNamespaceProvider implements NamespaceProvider {
    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        PsiFile[] filesByName = FilenameIndex.getFilesByName(element.getProject(), "ext_localconf.php", GlobalSearchScope.allScope(element.getProject()));

        return ContainerUtil.emptyList();
    }
}
