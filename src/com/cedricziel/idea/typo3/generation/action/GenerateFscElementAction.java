package com.cedricziel.idea.typo3.generation.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.cedricziel.idea.typo3.psi.TYPO3ExtensionUtil;
import com.cedricziel.idea.typo3.util.ActionUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import org.apache.commons.lang.StringUtils;

public class GenerateFscElementAction extends AnAction {

    public GenerateFscElementAction() {
        super("FSC CE", "test", TYPO3CMSIcons.TYPO3_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }

        String elementName = Messages.showInputDialog(project, "Enter element name:", "New content element", TYPO3CMSIcons.TYPO3_ICON);
        if (StringUtils.isEmpty(elementName)) {
            return;
        }

        PsiDirectory[] psiDirectories = ActionUtil.findDirectoryFromActionEvent(anActionEvent);
        if (psiDirectories.length == 0) {
            return;
        }

        TYPO3ExtensionDefinition extensionDefinition = TYPO3ExtensionUtil.findContainingExtension(psiDirectories);
        if (extensionDefinition == null) {
            return;
        }


    }
}
