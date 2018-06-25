package com.cedricziel.idea.typoscript.lang;

import icons.TypoScriptIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TypoScriptFileType extends LanguageFileType {
    public static final TypoScriptFileType INSTANCE = new TypoScriptFileType();

    private TypoScriptFileType() {
        super(TypoScriptLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Simple file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Simple language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "simple";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return TypoScriptIcons.FILE;
    }
}
