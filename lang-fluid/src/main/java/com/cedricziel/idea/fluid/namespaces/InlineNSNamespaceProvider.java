package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.FluidNamespaceStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidRecursiveWalkingVisitor;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class InlineNSNamespaceProvider implements NamespaceProvider {
    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        FileViewProvider viewProvider = element.getContainingFile().getViewProvider();
        if (!viewProvider.getLanguages().contains(HTMLLanguage.INSTANCE)) {
            return ContainerUtil.emptyList();
        }

        PsiFile htmlFile = viewProvider.getPsi(FluidLanguage.INSTANCE);
        FluidInlineNsVisitor visitor = new FluidInlineNsVisitor();
        htmlFile.accept(visitor);

        return visitor.namespaces;
    }

    private static class FluidInlineNsVisitor extends FluidRecursiveWalkingVisitor {
        Collection<FluidNamespace> namespaces = new ArrayList<>();

        @Override
        public void visitNamespaceStatement(@NotNull FluidNamespaceStatement o) {
            if (o.getAlias() != null && o.getNamespace() != null) {
                namespaces.add(new FluidNamespace(o.getAlias(), o.getNamespace()));
            }

            super.visitNamespaceStatement(o);
        }
    }
}
