package com.cedricziel.idea.typo3;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class IdeHelper {
    /**
     * @author Daniel Espendiller <daniel@espendiller.net>
     */
    public static void notifyEnableMessage(final Project project) {
        Notification notification = new Notification(
            "TYPO3 CMS Plugin",
            "TYPO3 CMS Plugin",
            "Enable the TYPO3 CMS Plugin <a href=\"enable\">with auto configuration now</a>, open <a href=\"config\">Project Settings</a> or <a href=\"dismiss\">dismiss</a> further messages",
            NotificationType.INFORMATION
        );
        notification.setListener((notification1, event) -> {
            // handle html click events
            if ("config".equals(event.getDescription())) {
                // open settings dialog and show panel
                TYPO3CMSProjectSettings.showSettings(project);
            } else if ("enable".equals(event.getDescription())) {
                enablePluginAndConfigure(project);

                Notifications.Bus.notify(new Notification("TYPO3 CMS Plugin", "TYPO3 CMS Plugin", "Plugin enabled", NotificationType.INFORMATION), project);
            } else if ("dismiss".equals(event.getDescription())) {
                // user doesn't want to show notification again
                TYPO3CMSProjectSettings.getInstance(project).dismissEnableNotification = true;
            }

            notification1.expire();
        });

        Notifications.Bus.notify(notification, project);
    }

    private static void enablePluginAndConfigure(@NotNull Project project) {
        TYPO3CMSProjectSettings.getInstance(project).pluginEnabled = true;
    }
}
