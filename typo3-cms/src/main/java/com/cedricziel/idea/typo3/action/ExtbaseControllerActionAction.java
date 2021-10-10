package com.cedricziel.idea.typo3.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.CodeUtil;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.refactoring.PhpNameUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ExtbaseControllerActionAction extends AbstractDumbAwareAction {

    public ExtbaseControllerActionAction() {
        super("Action", "Creates a new action", TYPO3CMSIcons.TYPO3_ICON);
    }

    public void update(AnActionEvent event) {
        Project project = getEventProject(event);
        if (project == null) {
            this.setStatus(event, false);
            return;
        }

        if (DumbService.isDumb(project)) {
            this.setStatus(event, false);
            return;
        }

        DataContext dataContext = event.getDataContext();

        final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        final PhpClass targetClass = editor == null || file == null ? null : PhpCodeEditUtil.findClassAtCaret(editor, file);

        if (targetClass == null) {
            setStatus(event, false);
            return;
        }

        if (ExtensionUtility.getExtensionDirectory(event) == null) {
            this.setStatus(event, false);
            return;
        }

        this.setStatus(event, PhpElementsUtil.hasSuperClass(targetClass, "\\TYPO3\\CMS\\Extbase\\Mvc\\Controller\\AbstractController"));
    }

    /**
     * @param event Carries information on the invocation place
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = getEventProject(event);
        if (project == null) {
            this.setStatus(event, false);
            return;
        }

        DataContext dataContext = event.getDataContext();

        final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        final PhpClass targetClass = editor == null || file == null ? null : PhpCodeEditUtil.findClassAtCaret(editor, file);

        if (targetClass == null) {
            JOptionPane.showMessageDialog(null, "Could not find containing class");
            return;
        }

        String actionName = Messages.showInputDialog(project, "New action name:", "New Extbase ActionController Action", TYPO3CMSIcons.TYPO3_ICON);

        if (StringUtils.isBlank(actionName)) {
            return;
        }

        actionName = Character.toLowerCase(actionName.charAt(0)) + actionName.substring(1);

        if (!actionName.endsWith("Action")) {
            actionName += "Action";
        }

        if (!PhpNameUtil.isValidMethodName(actionName)) {
            JOptionPane.showMessageDialog(null, "Invalid method name");
            return;
        }

        write(project, targetClass, actionName);
    }

    private void write(@NotNull Project project, PhpClass phpClass, String actionName) {
        WriteCommandAction.writeCommandAction(project).run(() -> {
            Method actionMethod = PhpPsiElementFactory.createMethod(
                project,
                "public function " + actionName + " () { \n" +
                    "}\n"

            );

            final Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, phpClass.getContainingFile().getVirtualFile()), true);
            if (editor == null) {
                return;
            }

            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            final int insertPos = CodeUtil.getMethodInsertPosition(phpClass, actionName);
            if (insertPos == -1) {
                return;
            }

            StringBuffer textBuf = new StringBuffer();
            textBuf.append("\n");
            textBuf.append(actionMethod.getText());

            editor.getDocument().insertString(insertPos, textBuf);
            final int endPos = insertPos + textBuf.length();

            CodeStyleManager.getInstance(project).reformatText(phpClass.getContainingFile(), insertPos, endPos);
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            Method insertedMethod = phpClass.findMethodByName(actionName);
            if (insertedMethod != null) {
                editor.getCaretModel().moveToOffset(insertedMethod.getTextRange().getStartOffset());
                editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            }
        });
    }
}
