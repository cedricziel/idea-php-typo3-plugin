package com.cedricziel.idea.fluid.lang.psi;

import com.cedricziel.idea.fluid.config.FluidConfig;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutor;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * TODO: This needs tweaking. While it's good to be able to substitute, we need more.
 */
public class FluidLanguageSubstitutor extends LanguageSubstitutor {
    @Nullable
    @Override
    public Language getLanguage(@NotNull VirtualFile file, @NotNull Project project) {
        if (FluidConfig.shouldOpenHtmlAsFluid(project) && file.getFileType() == HtmlFileType.INSTANCE) {
            if (file instanceof LightVirtualFile) {
                return null;
            }

            return FluidLanguage.INSTANCE;
        }

        return null;
    }
}
