package com.cedricziel.idea.typo3.projectTemplate;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.intellij.lang.javascript.boilerplate.AbstractGithubTagDownloadedProjectGenerator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.templates.github.GithubTagInfo;
import com.intellij.util.ActionRunner;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TYPO3CMSClassicLayoutDirectoryProjectGenerator extends AbstractGithubTagDownloadedProjectGenerator {
    @NotNull
    @Override
    protected String getDisplayName() {
        return "TYPO3 CMS Classic project layout";
    }

    @NotNull
    @Override
    public String getGithubUserName() {
        return "TYPO3";
    }

    @NotNull
    @Override
    public String getGithubRepositoryName() {
        return "TYPO3.CMS";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "The classic layout";
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

        try {
            ActionRunner.runInsideWriteAction(() -> {
                final VirtualFile extDir = VfsUtil.createDirectoryIfMissing(baseDir, "typo3conf/ext");
            });

        } catch (Exception e) {
            Messages.showErrorDialog("There was an error when trying to create additional project files", "Error Creating Additional Files");
        }
    }
}

