package com.cedricziel.idea.fluid.codeInsight.editorActions;

import com.cedricziel.idea.fluid.codeStyle.FluidCodeStyleSettings;
import com.cedricziel.idea.fluid.file.FluidFileViewProvider;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;

public class FluidTypedHandler extends TypedHandlerDelegate {
    public FluidTypedHandler() {
    }

    @NotNull
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        if (!(file.getViewProvider() instanceof FluidFileViewProvider)) {

            return Result.CONTINUE;
        } else if (c != '{') {

            return Result.CONTINUE;
        } else {
            int offset = editor.getCaretModel().getOffset();

            StringBuilder buf = new StringBuilder();
            String space = isWhitespaceRequired(project, c) ? " " : "";
            buf.append(c).append(space);
            if (isClosingSequenceNeeded(editor, offset, c)) {
                buf.append(space).append('}');
            }

            typeInStringAndMoveCaret(editor, buf.toString(), 1 + space.length());
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            documentManager.commitDocument(editor.getDocument());

            return Result.STOP;
        }
    }

    private static boolean isClosingSequenceNeeded(Editor editor, int afterOffset, char openingChar) {
        char closingChar = openingChar == '{' ? 125 : openingChar;
        CharSequence docCharSequence = editor.getDocument().getCharsSequence();

        for(int offset = afterOffset; offset < docCharSequence.length(); ++offset) {
            char currChar = docCharSequence.charAt(offset);
            char nextChar = offset + 1 < docCharSequence.length() ? docCharSequence.charAt(offset + 1) : 0;
            if (currChar == '{' && nextChar == openingChar) {
                return true;
            }

            if (currChar == closingChar && nextChar == '}') {
                return false;
            }
        }

        return true;
    }

    private static void typeInStringAndMoveCaret(Editor editor, String str, int caretShift) {
        EditorModificationUtil.insertStringAtCaret(editor, str, true, caretShift);
    }

    private static boolean isWhitespaceRequired(Project project, char c) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getInstance(project).getCurrentSettings();
        FluidCodeStyleSettings options = settings.getCustomSettings(FluidCodeStyleSettings.class);

        switch(c) {
        case '{':
            return options.SPACES_INSIDE_VARIABLE_DELIMITERS;
        default:
            return false;
        }
    }
}
