package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class ExtensionFileGenerationUtil {
    /**
     * @param templateFile        Name of the generated file
     * @param destinationPath     Relative path to the target file system entry
     * @param extensionDefinition Extension definition containing all relevant metadata
     * @param context             Template Context variables
     * @param project             Project in context
     */
    public static PsiElement fromTemplate(@NotNull String templateFile, @NotNull String destinationPath, @NotNull String destinationFileName, @NotNull TYPO3ExtensionDefinition extensionDefinition, @NotNull Map<String, String> context, Project project) {
        String template = getFileTemplateContent("/resources/fileTemplates/" + templateFile);
        for (Map.Entry<String, String> entry : context.entrySet()) {
            template = template.replace("{{ " + entry.getKey() + " }}", entry.getValue());
        }

        VirtualFile targetDirectory = getOrCreateDestinationPath(extensionDefinition.getRootDirectory(), destinationPath);

        PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText(destinationFileName, FileTypes.PLAIN_TEXT, template);
        CodeStyleManager.getInstance(project).reformat(fileFromText);
        return PsiDirectoryFactory
                .getInstance(project)
                .createDirectory(targetDirectory)
                .add(fileFromText);
    }

    private static VirtualFile getOrCreateDestinationPath(VirtualFile rootDirectory, String destinationPath) {
        try {
            return VfsUtil.createDirectoryIfMissing(rootDirectory, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static String getFileTemplateContent(@NotNull String filename) {
        try {
            return StreamUtil
                    .readText(ExtensionFileGenerationUtil.class.getResourceAsStream(filename), "UTF-8")
                    .replace("\r\n", "\n");
        } catch (IOException e) {
            return null;
        }
    }
}
