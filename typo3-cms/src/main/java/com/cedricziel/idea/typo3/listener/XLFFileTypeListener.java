package com.cedricziel.idea.typo3.listener;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class XLFFileTypeListener implements ProjectManagerListener {
    private static final NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup(
        "TYPO3 CMS Plugin: XLF File Type Association",
        NotificationDisplayType.BALLOON, true
    );

    @Override
    public void projectOpened(@NotNull Project project) {
        if (!(FileTypeManager.getInstance().getFileTypeByExtension("xlf") instanceof XmlFileType)) {
            WriteCommandAction.runWriteCommandAction(ProjectManager.getInstance().getOpenProjects()[0], () -> {
                FileTypeManager.getInstance().associateExtension(XmlFileType.INSTANCE, "xlf");

                ApplicationManager.getApplication().invokeLater(() -> {
                    Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(
                        "TYPO3 CMS Plugin",
                        "XLF File Type Association",
                        "The XLF File Type was re-assigned to XML to prevent errors with the XLIFF Plugin and allow autocompletion. Please re-index your projects.",
                        NotificationType.INFORMATION
                    );
                    Project[] projects = ProjectManager.getInstance().getOpenProjects();
                    Notifications.Bus.notify(notification, projects[0]);
                });
            });
        }
    }
}
