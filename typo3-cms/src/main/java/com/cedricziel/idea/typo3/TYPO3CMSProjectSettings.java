package com.cedricziel.idea.typo3;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "typo3cmsproject",
    storages = {
        @Storage("typo3-cms.xml")
    }
)
public class TYPO3CMSProjectSettings implements PersistentStateComponent<TYPO3CMSProjectSettings> {

    public boolean pluginEnabled;
    public boolean dismissEnableNotification;
    public boolean iconAnnotatorEnabled;
    public boolean routeAnnotatorEnabled;
    public boolean translationEnableTextFolding;
    public String translationFoldingLocale;

    public TYPO3CMSProjectSettings() {
        this.pluginEnabled = false;

        this.dismissEnableNotification = false;

        this.iconAnnotatorEnabled = true;
        this.routeAnnotatorEnabled = true;

        this.translationEnableTextFolding = true;
        this.translationFoldingLocale = "en";
    }

    public static TYPO3CMSProjectSettings getInstance(@NotNull Project project) {

        return ServiceManager.getService(project, TYPO3CMSProjectSettings.class);
    }

    public static boolean isEnabled(@NotNull PsiElement element) {

        TYPO3CMSProjectSettings instance = getInstance(element.getProject());
        if (instance == null) {
            return false;
        }

        return instance.pluginEnabled;
    }

    public static boolean isEnabled(@NotNull Project project) {

        return getInstance(project).pluginEnabled;
    }

    public static void showSettings(@NotNull Project project) {
        ShowSettingsUtilImpl.showSettingsDialog(project, "TYPO3CMS.SettingsForm", null);
    }

    public static String[] getAvailableLocales() {
        return new String[]{"en", "de"};
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
