package com.cedricziel.idea.typo3.generation.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.cedricziel.idea.typo3.psi.TYPO3ExtensionUtil;
import com.cedricziel.idea.typo3.util.ActionUtil;
import com.cedricziel.idea.typo3.util.ExtensionFileGenerationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateFscElementAction extends AnAction {

    public GenerateFscElementAction() {
        super(
                "Fluid Styled Content Element",
                "Generates a fluid_styled_content element",
                TYPO3CMSIcons.TYPO3_ICON
        );
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }

        ToolWindow toolWindow = anActionEvent.getData(PlatformDataKeys.TOOL_WINDOW);
        if (toolWindow == null) {
            return;
        }

        PsiDirectory[] psiDirectories = ActionUtil.findDirectoryFromActionEvent(anActionEvent);
        if (psiDirectories.length == 0) {
            return;
        }

        TYPO3ExtensionDefinition extensionDefinition = TYPO3ExtensionUtil.findContainingExtension(psiDirectories);
        if (extensionDefinition == null) {
            Messages.showErrorDialog(
                    "Could not extract extension from working directory. Does your extension contain a composer manifest?",
                    "Error While Trying to Find Extension"
            );
            return;
        }

        GenerateFscElementForm.create(toolWindow.getComponent(), project, extensionDefinition);
    }
}
