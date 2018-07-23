package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.webcore.template.TemplateLanguageFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTemplateHighlighter extends LayeredLexerEditorHighlighter {
    public FluidTemplateHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        super(new FluidFileHighlighter(), colors);

        FileType fileType = null;
        if (project != null && virtualFile != null) {
            fileType = TemplateLanguageFileUtil.getTemplateDataLanguage(project, virtualFile).getAssociatedFileType();
        }

        if (fileType == null) {
            fileType = FileTypes.PLAIN_TEXT;
        }

        this.registerLayer(FluidTypes.TEXT, new LayerDescriptor(SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, project, virtualFile), ""));
    }
}
