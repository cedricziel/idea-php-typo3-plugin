package com.cedricziel.idea.typo3.configuration;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
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
        return panel;
    }

    @Override
    public boolean isModified() {

        return !enablePlugin.isSelected() == getSettings().pluginEnabled;
    }

    @Override
    public void apply() throws ConfigurationException {
        getSettings().pluginEnabled = enablePlugin.isSelected();
    }

    public TYPO3CMSProjectSettings getSettings() {
        return TYPO3CMSProjectSettings.getInstance(project);
    }

    private void updateUIFromSettings() {
        enablePlugin.setSelected(getSettings().pluginEnabled);
    }
}
