package com.cedricziel.idea.typo3;

import com.cedricziel.idea.typo3.index.*;
import com.cedricziel.idea.typo3.index.extbase.ControllerActionIndex;
import com.cedricziel.idea.typo3.index.extensionScanner.MethodArgumentDroppedIndex;
import com.cedricziel.idea.typo3.index.php.LegacyClassesForIDEIndex;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TYPO3CMSProjectComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getInstance("TYPO3-CMS-Plugin");

    private Project project;

    public TYPO3CMSProjectComponent(Project project) {
        this.project = project;
    }

    public static Logger getLogger() {
        return LOG;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "com.cedricziel.idea.typo3.TYPO3CMSProjectComponent";
    }

    @Override
    public void projectOpened() {
        this.checkProject();

        TYPO3CMSSettings instance = TYPO3CMSSettings.getInstance(project);
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("com.cedricziel.idea.typo3"));
        if (plugin == null) {
            return;
        }

        String version = instance.getVersion();
        if (version == null || !plugin.getVersion().equals(version)) {
            instance.setVersion(plugin.getVersion());

            FileBasedIndex index = FileBasedIndex.getInstance();
            index.scheduleRebuild(CoreServiceMapStubIndex.KEY, new Throwable());
            index.scheduleRebuild(ExtensionNameStubIndex.KEY, new Throwable());
            index.scheduleRebuild(IconIndex.KEY, new Throwable());
            index.scheduleRebuild(ResourcePathIndex.KEY, new Throwable());
            index.scheduleRebuild(RouteIndex.KEY, new Throwable());
            index.scheduleRebuild(TablenameFileIndex.KEY, new Throwable());
            index.scheduleRebuild(LegacyClassesForIDEIndex.KEY, new Throwable());
            index.scheduleRebuild(MethodArgumentDroppedIndex.KEY, new Throwable());
            index.scheduleRebuild(ControllerActionIndex.KEY, new Throwable());
        }
    }

    @Override
    public void projectClosed() {
    }

    public void showInfoNotification(String content) {
        Notification notification = new Notification("TYPO3 CMS Plugin", "TYPO3 CMS Plugin", content, NotificationType.INFORMATION);

        Notifications.Bus.notify(notification, this.project);
    }

    public boolean isEnabled(@Nullable Project project) {

        return project != null && TYPO3CMSProjectSettings.getInstance(project).pluginEnabled;
    }

    private void checkProject() {
        if (!this.isEnabled(project) && !notificationIsDismissed() && containsPluginRelatedFiles()) {
            IdeHelper.notifyEnableMessage(project);
        }
    }

    private boolean notificationIsDismissed() {

        return TYPO3CMSProjectSettings.getInstance(project).dismissEnableNotification;
    }

    private boolean containsPluginRelatedFiles() {
        return (VfsUtil.findRelativeFile(this.project.getBaseDir(), "vendor", "typo3") != null)
            || FilenameIndex.getFilesByName(project, "ext_emconf.php", GlobalSearchScope.allScope(project)).length > 0;
    }
}
