package com.cedricziel.idea.typo3.generation.action;

import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.cedricziel.idea.typo3.util.Slugify;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GenerateFscElementForm extends JDialog {

    private final Project project;

    private JTextField elementTitle;
    private JTextField elementName;
    private JTextArea description;
    private JComboBox icon;
    private JButton generateButton;
    private JButton cancelButton;
    private JPanel panel;

    private  Slugify slugger;

    public GenerateFscElementForm(@NotNull Project project) {
        this.project = project;

        IconProvider iconProvider = IconProvider.getInstance(project);
        slugger = new Slugify().withUnderscoreSeparator(true);

        iconProvider.all().stream();

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (TYPO3IconDefinition iconDefinition : iconProvider.all()) {
            model.addElement(iconDefinition.getName());
        }

        this.icon.setModel(model);

        cancelButton.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        generateButton.addActionListener(e -> onGenerate());

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

    private void onTitleChange() {
        this.elementName.setText(slugger.slugify(elementTitle.getText()));
    }

    private void onGenerate() {

    }

    private void onCancel() {
        dispose();
    }

    public static GenerateFscElementForm create(@NotNull Component c, @NotNull Project project) {
        GenerateFscElementForm frame = new GenerateFscElementForm(project);
        frame.setTitle("Generate Fluid Styled Content Element");
        frame.setContentPane(frame.panel);
        frame.setModal(true);
        frame.setLocationRelativeTo(c);

        frame.pack();
        frame.setVisible(true);

        return frame;
    }
}
