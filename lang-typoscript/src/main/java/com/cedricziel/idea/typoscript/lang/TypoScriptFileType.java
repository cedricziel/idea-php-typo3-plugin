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
        return "TypoScript";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "TypoScript file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "typoscript";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return TypoScriptIcons.FILE;
    }


}
