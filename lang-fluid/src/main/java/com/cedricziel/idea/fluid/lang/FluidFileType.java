package com.cedricziel.idea.fluid.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import icons.FluidIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.Charset;

public class FluidFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final FluidFileType INSTANCE = new FluidFileType();

    protected FluidFileType() {
        super(FluidLanguage.INSTANCE);

        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, (project, fileType, virtualFile, colors) -> new FluidTemplateHighlighter(project, virtualFile, colors));
    }

    private static LanguageFileType getAssociatedFileType(VirtualFile file, Project project) {
        if (project == null) {
            return null;
        }
        Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(file);

        LanguageFileType associatedFileType = null;
        if (language != null) {
            associatedFileType = language.getAssociatedFileType();
        }

        if (language == null || associatedFileType == null) {
            associatedFileType = FluidLanguage.getDefaultTemplateLang();
        }

        return associatedFileType;
    }

    @NotNull
    @Override
    public String getName() {
        return "Fluid";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Fluid";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "fluid";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return FluidIcons.FILE;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte @NotNull [] content) {
        return CharsetToolkit.UTF8;
    }

    public Charset extractCharsetFromFileContent(@Nullable final Project project,
                                                 @Nullable final VirtualFile file,
                                                 @NotNull final CharSequence content) {
        LanguageFileType associatedFileType = getAssociatedFileType(file, project);

        if (associatedFileType == null) {
            return null;
        }

        return CharsetUtil.extractCharsetFromFileContent(project, file, associatedFileType, content);
    }
}
