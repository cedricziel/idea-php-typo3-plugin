package com.cedricziel.idea.typo3.projectTemplate;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.intellij.lang.javascript.boilerplate.AbstractGithubTagDownloadedProjectGenerator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.templates.github.GithubTagInfo;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TYPO3CMSComposerLayoutDirectoryProjectGenerator extends AbstractGithubTagDownloadedProjectGenerator {
    @NotNull
    @Override
    protected String getDisplayName() {
        return "TYPO3 CMS composer based project";
    }

    @NotNull
    @Override
    public String getGithubUserName() {
        return "TYPO3-Distributions";
    }

    @NotNull
    @Override
    public String getGithubRepositoryName() {
        return "TYPO3.CMS.BaseDistribution";
    }

    @Override
    public String getDescription() {
        return "TYPO3 Project";
    }

    @Override
    public Icon getIcon() {
        return TYPO3CMSIcons.TYPO3_ICON;
    }

    @Override
    public boolean isPrimaryGenerator() {
        return PlatformUtils.isPhpStorm();
    }

    @Nullable
    @Override
    public String getPrimaryZipArchiveUrlForDownload(@NotNull GithubTagInfo tag) {
        return null;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull GithubTagInfo tag, @NotNull Module module) {
        super.generateProject(project, baseDir, tag, module);

        Messages.showInfoMessage(project, "Please update the composer dependencies to create the initial structure", "TYPO3 Project Created");
    }
}

