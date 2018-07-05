package com.cedricziel.idea.fluid.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidLanguage extends Language implements TemplateLanguage {
    public static final FluidLanguage INSTANCE = new FluidLanguage();

    @SuppressWarnings("SameReturnValue")
    public static LanguageFileType getDefaultTemplateLang() {
        return StdFileTypes.HTML;
    }

    private FluidLanguage() {
        super("Fluid");
    }

    public FluidLanguage(@Nullable Language baseLanguage, @NotNull @NonNls final String ID, @NotNull @NonNls final String... mimeTypes) {
        super(baseLanguage, ID, mimeTypes);
    }
}
