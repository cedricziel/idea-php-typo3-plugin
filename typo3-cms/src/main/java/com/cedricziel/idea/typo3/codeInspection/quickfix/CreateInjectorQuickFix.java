package com.cedricziel.idea.typo3.codeInspection.quickfix;

import com.cedricziel.idea.typo3.util.CodeUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThrowableRunnable;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class CreateInjectorQuickFix implements LocalQuickFix {

    private final SmartPsiElementPointer<PhpDocTag> element;

    public CreateInjectorQuickFix(@NotNull PhpDocTag element) {
        this.element = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Refactor to method injection";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Dependency Injection";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PhpDocComment docCommentElement = (PhpDocComment) element.getElement().getParent();
        PsiElement owner = docCommentElement.getOwner();
        if (!(owner instanceof Field)) {
            return;
        }
        Field field = (Field) owner;

        final String fieldName = field.getName();
        final PhpClass containingClass = PsiTreeUtil.getParentOfType(element.getElement(), PhpClass.class);
        if (containingClass == null) {
            return;
        }

        PhpType type = field.getType();
        if (type == PhpType.SCALAR) {
            // Scalar type - can't inject those, heh.
            return;
        }

        final String methodName = "inject" + StringUtils.capitalize(fieldName);
        Method injectorFunction = PhpPsiElementFactory.createMethod(
            project,
            "public function " + methodName + " (" + type + " $" + fieldName + ") {\n" +
                "  $this->" + fieldName + " = $" + fieldName + ";\n" +
                "}\n"

        );

        final Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, element.getContainingFile().getVirtualFile()), true);
        if (editor == null) {
            return;
        }

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        final int insertPos = CodeUtil.getMethodInsertPosition(containingClass, methodName);
        if (insertPos == -1) {
            return;
        }

        try {
            WriteCommandAction.writeCommandAction(project, element.getContainingFile())
                .withGroupId("Create Injection Method")
                .run((ThrowableRunnable<Throwable>) () -> {
                    StringBuffer textBuf = new StringBuffer();
                    textBuf.append("\n");
                    textBuf.append(injectorFunction.getText());

                    editor.getDocument().insertString(insertPos, textBuf);
                    final int endPos = insertPos + textBuf.length();

                    CodeStyleManager.getInstance(project).reformatText(containingClass.getContainingFile(), insertPos, endPos + 1);
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

                    CodeStyleManager.getInstance(project).reformatText(docCommentElement.getContainingFile(), Collections.singletonList(docCommentElement.getTextRange()));
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

                    Method insertedMethod = containingClass.findMethodByName(methodName);
                    if (insertedMethod != null) {
                        editor.getCaretModel().moveToOffset(insertedMethod.getTextRange().getStartOffset());
                        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);

                        element.getElement().delete();
                    }
                });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
