package com.cedricziel.idea.typo3.projectTemplate;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TYPO3CMSProjectTemplatesFactory extends ProjectTemplatesFactory {
    @NotNull
    @Override
    public String @NotNull [] getGroups() {
        return new String[]{"PHP"};
    }

    @NotNull
    @Override
    public ProjectTemplate @NotNull [] createTemplates(@Nullable String group, WizardContext context) {
        return new ProjectTemplate[]{
                new TYPO3CMSClassicLayoutDirectoryProjectGenerator(),
                new TYPO3CMSComposerLayoutDirectoryProjectGenerator()
        };
    }
}
