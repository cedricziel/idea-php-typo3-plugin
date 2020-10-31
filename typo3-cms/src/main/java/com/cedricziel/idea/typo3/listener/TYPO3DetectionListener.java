package com.cedricziel.idea.typo3.listener;

import com.cedricziel.idea.typo3.IdeHelper;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.TYPO3CMSSettings;
import com.cedricziel.idea.typo3.index.*;
import com.cedricziel.idea.typo3.index.extbase.ControllerActionIndex;
import com.cedricziel.idea.typo3.index.extensionScanner.MethodArgumentDroppedIndex;
import com.cedricziel.idea.typo3.index.php.LegacyClassesForIDEIndex;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TYPO3DetectionListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        this.checkProject(project);

        TYPO3CMSSettings instance = TYPO3CMSSettings.getInstance(project);
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("com.cedricziel.idea.typo3"));
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

    public boolean isEnabled(@Nullable Project project) {

        return project != null && TYPO3CMSProjectSettings.getInstance(project).pluginEnabled;
    }

    private void checkProject(@NotNull Project project) {
        if (!this.isEnabled(project) && !notificationIsDismissed(project) && containsPluginRelatedFiles(project)) {
            IdeHelper.notifyEnableMessage(project);
        }
    }

    private boolean notificationIsDismissed(@NotNull Project project) {

        return TYPO3CMSProjectSettings.getInstance(project).dismissEnableNotification;
    }

    private boolean containsPluginRelatedFiles(@NotNull Project project) {
        return (VfsUtil.findRelativeFile(project.getBaseDir(), "vendor", "typo3") != null)
            || FilenameIndex.getFilesByName(project, "ext_emconf.php", GlobalSearchScope.allScope(project)).length > 0;
    }
}
