package com.cedricziel.idea.typo3.generation.action;

import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.cedricziel.idea.typo3.util.ExtensionFileGenerationUtil;
import com.cedricziel.idea.typo3.util.Slugify;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class GenerateFscElementForm extends JDialog {

    private final Project project;
    private final TYPO3ExtensionDefinition extensionDefinition;

    private JTextField elementTitle;
    private JTextField elementName;
    private JTextArea elementDescription;
    private JComboBox icon;
    private JButton generateButton;
    private JButton cancelButton;
    private JPanel panel;

    private Slugify slugger;

    public GenerateFscElementForm(@NotNull Project project, TYPO3ExtensionDefinition extensionDefinition) {
        this.project = project;
        this.extensionDefinition = extensionDefinition;

        IconProvider iconProvider = IconProvider.getInstance(project);
        slugger = new Slugify().withUnderscoreSeparator(true);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (TYPO3IconDefinition iconDefinition : iconProvider.all(project)) {
            model.addElement(iconDefinition.getIdentifier());
        }

        this.icon.setModel(model);

        cancelButton.addActionListener(e -> onCancel());
        generateButton.addActionListener(e -> onGenerate());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        elementTitle.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                onTitleChange();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                onTitleChange();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                onTitleChange();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    public static GenerateFscElementForm create(@NotNull Component c, @NotNull Project project, TYPO3ExtensionDefinition extensionDefinition) {
        GenerateFscElementForm frame = new GenerateFscElementForm(project, extensionDefinition);
        frame.setTitle("Generate Fluid Styled Content Element");
        frame.setContentPane(frame.panel);
        frame.setModal(true);
        frame.setLocationRelativeTo(c);

        frame.pack();
        frame.setVisible(true);

        return frame;
    }

    private void onTitleChange() {
        this.elementName.setText(slugger.slugify(elementTitle.getText()));
    }

    private void onGenerate() {
        ApplicationManager.getApplication().runWriteAction(new Thread(this::generate));
    }

    private void onCancel() {
        dispose();
    }

    /**
     * Does the actual code generation.
     */
    private void generate() {

        String formElementName = this.elementName.getText();
        String formElementTitle = this.elementTitle.getText();
        String formElementDescription = this.elementDescription.getText();

        String successMessage = "New Content Element \"" + formElementName + "\" in extension " + extensionDefinition.getExtensionKey() + " successfully created.";
        String errorMessageOverridesExist = "The TCA definition file for the element already exists. Unsupported operation.";

        // Exit if element exists. Maybe one day... *sigh*
        if (ExtensionFileGenerationUtil.extensionHasFile(extensionDefinition, "Configuration/TCA/Overrides/tt_content_element_" + formElementName + ".php")) {
            Notification notification = new Notification(
                    "TYPO3 CMS Plugin",
                    "TYPO3 CMS",
                    errorMessageOverridesExist,
                    NotificationType.ERROR
            );
            Notifications.Bus.notify(notification, this.project);

            this.dispose();
            return;
        }

        /*
         * Build template context. It will be available in the templates through '{{ marker }}' markers
         */
        Map<String, String> context = new HashMap<>();
        context.put("elementName", formElementName);
        context.put("elementTitle", formElementTitle);
        context.put("elementDescription", formElementDescription);
        context.put("extensionKey", extensionDefinition.getExtensionKey());
        context.put("templateName", StringUtils.capitalize(formElementName) + ".html");
        context.put("icon", (String) this.icon.getSelectedItem());

        /*
         * Generate element main TypoScript
         */
        PsiElement element = ExtensionFileGenerationUtil.fromTemplate(
                "contentElement/fsc/ts_setup.typoscript",
                "Configuration/TypoScript/ContentElement",
                formElementName + ".typoscript",
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
                formElementName +
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
                "tt_content_element_" + formElementName + ".php",
                extensionDefinition,
                context,
                project
        );
        new OpenFileDescriptor(project, elementTcaOverrides.getContainingFile().getVirtualFile(), 0).navigate(true);

        if (!ExtensionFileGenerationUtil.extensionHasFile(extensionDefinition, "Configuration/TCA/Overrides/sys_template.php")) {
            /*
             * Generate static template imports
             */
            PsiElement sysTemplateImport = ExtensionFileGenerationUtil.fromTemplate(
                    "contentElement/fsc/tca_overrides_systemplate.php",
                    "Configuration/TCA/Overrides",
                    "sys_template.php",
                    extensionDefinition,
                    context,
                    project
            );
            new OpenFileDescriptor(project, sysTemplateImport.getContainingFile().getVirtualFile(), 0).navigate(true);
        } else {
            successMessage += "\nsys_template overrides already exist. Please check that the static template is added.";
        }

        Notification notification = new Notification(
                "TYPO3 CMS Plugin",
                "TYPO3 CMS",
                successMessage,
                NotificationType.INFORMATION
        );
        Notifications.Bus.notify(notification, this.project);

        this.dispose();
    }
}
