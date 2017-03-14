package com.cedricziel.idea.typo3;

import com.cedricziel.idea.typo3.container.IconProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TYPO3CMSProjectComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getInstance("TYPO3-CMS-Plugin");

    private Project project;

    public TYPO3CMSProjectComponent(Project project) {
        this.project = project;
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
        Project ref = this.project;
        ApplicationManager.getApplication().executeOnPooledThread(() -> ApplicationManager.getApplication().runReadAction(() -> {
            IconProvider.getInstance(ref);
        }));
    }

    @Override
    public void projectClosed() {
        IconProvider.destroyInstance(project);
    }

    public static Logger getLogger() {
        return LOG;
    }
}
