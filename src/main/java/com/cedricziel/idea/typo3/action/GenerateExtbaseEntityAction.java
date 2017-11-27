package com.cedricziel.idea.typo3.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.util.ExtensionFileGenerationUtil;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GenerateExtbaseEntityAction extends NewExtensionFileAction {

    public GenerateExtbaseEntityAction() {

        super("Extbase Entity", "Generate a Extbase Entity", TYPO3CMSIcons.TYPO3_ICON);
    }

    @Override
    protected void write(@NotNull Project project, @NotNull PsiDirectory extensionRootDirectory, @NotNull String className) {

        final String finalClassName = className;
        RunResult<PsiElement> elementRunResult = new WriteCommandAction<PsiElement>(project) {

            @Override
            protected void run(@NotNull Result result) throws Throwable {
                PsiElement extensionFile;
                Map<String, String> context = new HashMap<>();

                String calculatedNamespace = ExtensionUtility.findDefaultNamespace(extensionRootDirectory);
                if (calculatedNamespace == null) {
                    result.setResult(null);
                    return;
                }

                calculatedNamespace += "Domain\\Entity";

                context.put("namespace", calculatedNamespace);
                context.put("className", finalClassName);

                try {
                    extensionFile = ExtensionFileGenerationUtil.fromTemplate(
                            "extension_file/ExtbaseEntity.php",
                            "Classes/Domain/Entity",
                            finalClassName + ".php",
                            extensionRootDirectory,
                            context,
                            project
                    );

                    if (extensionFile != null) {
                        result.setResult(extensionFile);
                    }
                } catch (IncorrectOperationException e) {
                    // file already exists
                }
            }
        }.execute();

        if (elementRunResult.getResultObject() != null) {
            new OpenFileDescriptor(project, elementRunResult.getResultObject().getContainingFile().getVirtualFile(), 0).navigate(true);
        } else {
            Messages.showErrorDialog("Cannot create extension file", "Error");
        }
    }
}
