package com.cedricziel.idea.fluid.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.FluidIcons;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class FluidFileType extends LanguageFileType {
    public static final FluidFileType INSTANCE = new FluidFileType();

    private FluidFileType() {
        super(FluidLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Fluid Template";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Simple Fluid Template";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "html";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return FluidIcons.FILE;
    }
}
