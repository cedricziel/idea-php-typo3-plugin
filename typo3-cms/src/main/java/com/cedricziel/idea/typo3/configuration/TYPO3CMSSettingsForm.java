package com.cedricziel.idea.typo3.configuration;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TYPO3CMSSettingsForm implements Configurable {
    @NotNull
    private final Project project;

    private JCheckBox enablePlugin;
    private JPanel panel;
    private JCheckBox iconAnnotatorEnabled;
    private JCheckBox routeAnnotatorEnabled;
    private JCheckBox translationEnableTextFolding;
    private JComboBox translationFavoriteLocale;
    private JCheckBox iconUsageGutterIconsEnabled;
    private JCheckBox iconDefinitionGutterIconsEnabled;

    public TYPO3CMSSettingsForm(@NotNull Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "TYPO3 CMS Plugin";
    }

    @Override
    public void reset() {
        updateUIFromSettings();
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        for (String availableLocale : TYPO3CMSProjectSettings.getAvailableLocales()) {
            translationFavoriteLocale.addItem(availableLocale);
        }

        return panel;
    }

    @Override
    public boolean isModified() {

        return enablePlugin.isSelected() != getSettings().pluginEnabled
            || iconAnnotatorEnabled.isSelected() != getSettings().iconAnnotatorEnabled
            || routeAnnotatorEnabled.isSelected() != getSettings().routeAnnotatorEnabled
            || translationEnableTextFolding.isSelected() != getSettings().translationEnableTextFolding
            || translationFavoriteLocale.getSelectedItem() != getSettings().translationFavoriteLocale
            || iconUsageGutterIconsEnabled.isSelected() != getSettings().iconUsageGutterIconsEnabled
            || iconDefinitionGutterIconsEnabled.isSelected() != getSettings().iconDefinitionGutterIconsEnabled
        ;
    }

    @Override
    public void apply() {
        getSettings().pluginEnabled = enablePlugin.isSelected();
        getSettings().iconAnnotatorEnabled = iconAnnotatorEnabled.isSelected();
        getSettings().routeAnnotatorEnabled = routeAnnotatorEnabled.isSelected();
        getSettings().iconUsageGutterIconsEnabled = iconUsageGutterIconsEnabled.isSelected();
        getSettings().iconDefinitionGutterIconsEnabled = iconDefinitionGutterIconsEnabled.isSelected();

        getSettings().translationEnableTextFolding = translationEnableTextFolding.isSelected();
        getSettings().translationFavoriteLocale = translationFavoriteLocale.getSelectedItem();
    }

    public TYPO3CMSProjectSettings getSettings() {
        return TYPO3CMSProjectSettings.getInstance(project);
    }

    private void updateUIFromSettings() {
        enablePlugin.setSelected(getSettings().pluginEnabled);
        iconAnnotatorEnabled.setSelected(getSettings().iconAnnotatorEnabled);
        routeAnnotatorEnabled.setSelected(getSettings().routeAnnotatorEnabled);
        iconUsageGutterIconsEnabled.setSelected(getSettings().iconUsageGutterIconsEnabled);
        iconDefinitionGutterIconsEnabled.setSelected(getSettings().iconDefinitionGutterIconsEnabled);

        translationEnableTextFolding.setSelected(getSettings().translationEnableTextFolding);
        translationFavoriteLocale.setSelectedItem(getSettings().translationFavoriteLocale);
    }
}
