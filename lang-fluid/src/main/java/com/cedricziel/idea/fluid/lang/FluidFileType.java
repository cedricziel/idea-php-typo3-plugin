package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.FluidBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import icons.FluidIcons;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.nio.charset.Charset;

public class FluidFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final FluidFileType INSTANCE = new FluidFileType();

    private FluidFileType() {
        this(FluidLanguage.INSTANCE);
    }

    protected FluidFileType(Language lang) {
        super(lang);

        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(
            this,
            new EditorHighlighterProvider() {
                public EditorHighlighter getEditorHighlighter(
                    @Nullable Project project,
                    @NotNull FileType fileType,
                    @Nullable VirtualFile virtualFile,
                    @NotNull EditorColorsScheme editorColorsScheme
                ) {
                    return new FluidTemplateHighlighter(project, virtualFile, editorColorsScheme);
                }
            }
        );
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
        return "Fluid Template";
    }

    @NotNull
    @Override
    public String getDescription() {
        return FluidBundle.message("fl.files.file.type.description");
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
