package com.cedricziel.idea.typo3.projectTemplate;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.TYPO3CMSSettings;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TYPO3CMSClassicLayoutDirectoryProjectGenerator extends WebProjectTemplate<TYPO3CMSSettings> {

    @Override
    public String getDescription() {
        return "TYPO3 Project";
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "TYPO3 Classic Layout";
    }

    @Override
    public Icon getIcon() {
        return TYPO3CMSIcons.TYPO3_ICON;
    }

    @Override
    public boolean isPrimaryGenerator() {
        return PlatformUtils.isPhpStorm();
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull TYPO3CMSSettings settings, @NotNull Module module) {

    }

    static class TYPO3InstallerSettings {

    }
}

