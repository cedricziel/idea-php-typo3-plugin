package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FluidPostfixTemplateProvider implements PostfixTemplateProvider {
    @NotNull
    @Override
    public Set<PostfixTemplate> getTemplates() {
        return ContainerUtil.set(
            new AliasPostfixTemplate(this),
            new ForEachPostfixTemplate(this),
            new DebugInlinePostfixTemplate(this)
        );
    }

    @Override
    public boolean isTerminalSymbol(char currentChar) {
        return '.' == currentChar;
    }

    @Override
    public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @Override
    public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @NotNull
    @Override
    public PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
        return copyFile;
    }
}
