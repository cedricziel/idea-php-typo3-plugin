package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class ExtensionFileGenerationUtil {

    /**
     * @param templateFile        Name of the generated file
     * @param context             Template Context variables
     */
    public static String readTemplateToString(@NotNull String templateFile, @NotNull Map<String, String> context) {
        String template = getFileTemplateContent("/resources/fileTemplates/" + templateFile);
        for (Map.Entry<String, String> entry : context.entrySet()) {
            template = template.replace("{{ " + entry.getKey() + " }}", entry.getValue());
        }

        return template;
    }

    /**
     * @param templateFile        Name of the generated file
     * @param destinationPath     Relative path to the target file system entry
     * @param extensionDefinition Extension definition containing all relevant metadata
     * @param context             Template Context variables
     * @param project             Project in context
     */
    public static PsiElement fromTemplate(@NotNull String templateFile, @NotNull String destinationPath, @NotNull String destinationFileName, @NotNull TYPO3ExtensionDefinition extensionDefinition, @NotNull Map<String, String> context, Project project) {
        String template = readTemplateToString(templateFile, context);

        VirtualFile targetDirectory = getOrCreateDestinationPath(extensionDefinition.getRootDirectory(), destinationPath);

        PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText(destinationFileName, FileTypes.PLAIN_TEXT, template);
        CodeStyleManager.getInstance(project).reformat(fileFromText);
        return PsiDirectoryFactory
                .getInstance(project)
                .createDirectory(targetDirectory)
                .add(fileFromText);
    }

    /**
     * Appends a string to a new or existing file
     *
     * @param templateString      The string to append. Variables with '{{ varname }}' syntax are substituted from context
     * @param destinationPath     Relative path inside the extension directory folder
     * @param destinationFileName The file name
     * @param extensionDefinition The containing extension
     * @param context             Template variable map
     * @param project             Current project
     * @return The created or modified file
     */
    public static VirtualFile appendOrCreate(@NotNull String templateString, @NotNull String destinationPath, @NotNull String destinationFileName, @NotNull TYPO3ExtensionDefinition extensionDefinition, @NotNull Map<String, String> context, Project project) {
        for (Map.Entry<String, String> entry : context.entrySet()) {
            templateString = templateString.replace("{{ " + entry.getKey() + " }}", entry.getValue());
        }

        VirtualFile targetDirectory = getOrCreateDestinationPath(extensionDefinition.getRootDirectory(), destinationPath);
        if (targetDirectory == null) {
            return null;
        }

        VirtualFile[] children = targetDirectory.getChildren();
        VirtualFile targetFile = null;
        for (VirtualFile file : children) {
            if (!file.isDirectory() && file.getName().equals(destinationFileName)) {
                targetFile = file;
            }
        }

        if (targetFile == null) {
            PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText(destinationFileName, FileTypes.PLAIN_TEXT, templateString);

            CodeStyleManager.getInstance(project).reformat(fileFromText);
            PsiDirectoryFactory
                    .getInstance(project)
                    .createDirectory(targetDirectory)
                    .add(fileFromText);

            return fileFromText.getVirtualFile();
        }

        PsiFile file = PsiManager.getInstance(project).findFile(targetFile);
        if (file == null) {
            return null;
        }

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = documentManager.getDocument(file);
        if (document == null) {
            return null;
        }

        String content = document.getText();
        StringBuilder contentSb = new StringBuilder(content);
        contentSb
                .append("\n")
                .append(templateString);
        document.setText(contentSb);

        return file.getVirtualFile();
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
