package com.cedricziel.idea.typo3.generation.action;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.cedricziel.idea.typo3.psi.TYPO3ExtensionUtil;
import com.cedricziel.idea.typo3.util.ActionUtil;
import com.cedricziel.idea.typo3.util.ExtensionFileGenerationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateFscElementAction extends AnAction {

    public GenerateFscElementAction() {
        super(
                "Fluid Styled Content Element",
                "Generates a fluid_styled_content element",
                TYPO3CMSIcons.TYPO3_ICON
        );
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }

        ToolWindow toolWindow = anActionEvent.getData(PlatformDataKeys.TOOL_WINDOW);
        if (toolWindow == null) {
            return;
        }

        GenerateFscElementForm form = GenerateFscElementForm.create(toolWindow.getComponent(), project);

        String elementName = Messages.showInputDialog(
                project,
                "Enter element name:",
                "New Content Element",
                TYPO3CMSIcons.TYPO3_ICON
        );

        // empty elementName also means the dialogue was aborted
        if (StringUtils.isEmpty(elementName)) {
            return;
        }

        if (!elementName.matches("\\w*")) {
            Messages.showErrorDialog(
                    "Could not create element. Element name should only contain [a-z0-9_]",
                    "Incorrect Content Element Name"
            );
            return;
        }

        new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                PsiDirectory[] psiDirectories = ActionUtil.findDirectoryFromActionEvent(anActionEvent);
                if (psiDirectories.length == 0) {
                    return;
                }

                TYPO3ExtensionDefinition extensionDefinition = TYPO3ExtensionUtil.findContainingExtension(psiDirectories);
                if (extensionDefinition == null) {
                    Messages.showErrorDialog(
                            "Could not extract extension from working directory. Does your extension contain a composer manifest?",
                            "Error While Trying to Find Extension"
                    );
                    return;
                }

                /*
                 * Build template context. It will be available in the templates through '{{ marker }}' markers
                 */
                Map<String, String> context = new HashMap<>();
                context.put("elementName", elementName);
                context.put("extensionKey", extensionDefinition.getExtensionKey());
                context.put("templateName", StringUtils.capitalize(elementName) + ".html");

                /*
                 * Generate element main TypoScript
                 */
                PsiElement element = ExtensionFileGenerationUtil.fromTemplate(
                        "contentElement/fsc/ts_setup.typoscript",
                        "Configuration/TypoScript/ContentElement",
                        elementName + ".typoscript",
                        extensionDefinition,
                        context,
                        project
                );
                new OpenFileDescriptor(project, element.getContainingFile().getVirtualFile(), 0).navigate(true);

                /*
                 * Generate element main fluid template
                 */
                PsiElement templateElement = ExtensionFileGenerationUtil.fromTemplate(
                        "contentElement/fsc/element.html",
                        "Resources/Private/Templates/ContentElements",
                        context.get("templateName"),
                        extensionDefinition,
                        context,
                        project
                );
                new OpenFileDescriptor(project, templateElement.getContainingFile().getVirtualFile(), 0).navigate(true);

                /*
                 * Generate element TypoScript include to main TS template
                 */
                String ceImport = "<INCLUDE_TYPOSCRIPT: source=\"FILE:EXT:" +
                        extensionDefinition.getExtensionKey() +
                        "/Configuration/TypoScript/ContentElement/" +
                        elementName +
                        ".typoscript\">";

                VirtualFile mainTsFile = ExtensionFileGenerationUtil.appendOrCreate(
                        ceImport,
                        "Configuration/TypoScript",
                        "setup.txt",
                        extensionDefinition,
                        context,
                        project
                );
                if (mainTsFile == null) {
                    return;
                }
                new OpenFileDescriptor(project, mainTsFile, 0).navigate(true);

                /*
                 * Generate New content element wizard tsconfig
                 */
                String newCeTsconfig = ExtensionFileGenerationUtil.readTemplateToString("contentElement/fsc/newcewizard.tsconfig", context);
                VirtualFile newCeTsConfigFile = ExtensionFileGenerationUtil.appendOrCreate(
                        newCeTsconfig,
                        "Configuration/PageTSconfig",
                        "NewContentElementWizard.tsconfig",
                        extensionDefinition,
                        context,
                        project
                );
                new OpenFileDescriptor(project, newCeTsConfigFile, 0).navigate(true);

                /*
                 * Generate element TCA overrides
                 */
                PsiElement elementTcaOverrides = ExtensionFileGenerationUtil.fromTemplate(
                        "contentElement/fsc/tca_overrides_ttcontent.php",
                        "Configuration/TCA/Overrides",
                        "tt_content_element" + elementName + ".php",
                        extensionDefinition,
                        context,
                        project
                );
                new OpenFileDescriptor(project, elementTcaOverrides.getContainingFile().getVirtualFile(), 0).navigate(true);
            }
        }.execute();
    }
}
