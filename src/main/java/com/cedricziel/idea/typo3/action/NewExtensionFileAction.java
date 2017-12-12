package com.cedricziel.idea.typo3.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.php.refactoring.PhpNameUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

abstract class NewExtensionFileAction extends AbstractDumbAwareAction {

    public NewExtensionFileAction(String create_viewHelper, String s, Icon typo3Icon) {
        super(create_viewHelper, s, typo3Icon);
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

        this.setStatus(event, ExtensionUtility.getExtensionDirectory(event) != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        final Project project = getEventProject(event);
        if (project == null) {
            this.setStatus(event, false);
            return;
        }

        PsiDirectory bundleDirContext = ExtensionUtility.getExtensionDirectory(event);
        if (bundleDirContext == null) {
            return;
        }

        String className = Messages.showInputDialog(project, "New class name:", "New File", TYPO3CMSIcons.TYPO3_ICON);
        if (StringUtils.isBlank(className)) {
            return;
        }

        if (!PhpNameUtil.isValidClassName(className)) {
            JOptionPane.showMessageDialog(null, "Invalid class name");
            return;
        }

        write(project, bundleDirContext, className);
    }

    abstract protected void write(@NotNull Project project, @NotNull PsiDirectory extensionRootDirectory, @NotNull String className);
}
