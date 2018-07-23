package com.cedricziel.idea.fluid.editor.highlighter;

import com.cedricziel.idea.fluid.lang.FluidTemplateHighlighter;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidEditorHighlighterFactory implements EditorHighlighterProvider {
    @Override
    public EditorHighlighter getEditorHighlighter(@Nullable Project project,
                                                  @NotNull FileType type,
                                                  @Nullable VirtualFile file,
                                                  @NotNull EditorColorsScheme scheme) {
        return new FluidTemplateHighlighter(project, file, scheme);
    }
}
