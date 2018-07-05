package com.cedricziel.idea.typo3.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

import javax.swing.*;

abstract class AbstractDumbAwareAction extends DumbAwareAction {
    public AbstractDumbAwareAction(String create_viewHelper, String s, Icon typo3Icon) {
        super(create_viewHelper, s, typo3Icon);
    }

    /**
     * Updates the state of the action. Default implementation does nothing.
     * Override this method to provide the ability to dynamically change action's
     * state and(or) presentation depending on the context (For example
     * when your action state depends on the selection you can check for
     * selection and change the state accordingly).
     * This method can be called frequently, for instance, if an action is added to a toolbar,
     * it will be updated twice a second. This means that this method is supposed to work really fast,
     * no real work should be done at this phase. For example, checking selection in a tree or a list,
     * is considered valid, but working with a file system is not. If you cannot understand the state of
     * the action fast you should do it in the {@link #actionPerformed(AnActionEvent)} method and notify
     * the user that action cannot be executed if it's the case.
     *
     * @param event Carries information on the invocation place and data available
     */
    @Override
    public void update(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            setStatus(event, false);
        }
    }

    protected void setStatus(AnActionEvent event, boolean status) {
        event.getPresentation().setVisible(status);
        event.getPresentation().setEnabled(status);
    }
}
