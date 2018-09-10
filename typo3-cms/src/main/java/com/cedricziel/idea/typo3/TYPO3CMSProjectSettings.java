package com.cedricziel.idea.typo3;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "typo3cmsproject",
    storages = {
        @Storage("/typo3-cms.xml")
    }
)
public class TYPO3CMSProjectSettings implements PersistentStateComponent<TYPO3CMSProjectSettings> {

    public boolean pluginEnabled;
    public boolean dismissEnableNotification;

    public TYPO3CMSProjectSettings() {
        this.pluginEnabled = false;
        this.dismissEnableNotification = false;
    }

    public static TYPO3CMSProjectSettings getInstance(@NotNull Project project) {

        return ServiceManager.getService(project, TYPO3CMSProjectSettings.class);
    }

    public static void showSettings(@NotNull Project project) {
        ShowSettingsUtilImpl.showSettingsDialog(project, "TYPO3CMS.SettingsForm", null);
    }

    @Nullable
    @Override
    public TYPO3CMSProjectSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TYPO3CMSProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
