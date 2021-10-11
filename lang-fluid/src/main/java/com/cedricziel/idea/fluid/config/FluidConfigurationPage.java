package com.cedricziel.idea.fluid.config;

import com.cedricziel.idea.fluid.FluidBundle;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.ui.ListCellRendererWrapper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FluidConfigurationPage implements SearchableConfigurable {
    private final Project myProject;
    private JCheckBox myAutoGenerateClosingTagCheckBox;
    private JPanel myWholePanel;
    private JCheckBox myFormattingCheckBox;
    private JComboBox myCommenterLanguage;
    private JCheckBox myAutocompleteMustaches;
    private JCheckBox htmlAsHb;

    public FluidConfigurationPage(Project project) {
        myProject = project;
    }

    @NotNull
    @Override
    public String getId() {
        return "editor.preferences.fluidOptions";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return FluidBundle.message("fl.pages.options.title");
    }

    @SuppressWarnings({"UnusedDeclaration", "SameReturnValue"})
    // this  can probably be deleted eventually; IDEA 11 expects it to be here
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getHelpTopic() {
        return "reference.settings.ide.settings.handlebars.mustache";
    }

    @Override
    public JComponent createComponent() {
        return myWholePanel;
    }

    @Override
    public boolean isModified() {
        return myAutoGenerateClosingTagCheckBox.isSelected() != FluidConfig.isAutoGenerateCloseTagEnabled()
            || myAutocompleteMustaches.isSelected() != FluidConfig.isAutocompleteMustachesEnabled()
            || myFormattingCheckBox.isSelected() != FluidConfig.isFormattingEnabled()
            || htmlAsHb.isSelected() != FluidConfig.shouldOpenHtmlAsFluid(myProject)
            || !FluidConfig.getCommenterLanguage().getID().equals(getSelectedLanguageId());
    }

    private String getSelectedLanguageId() {
        final Object item = myCommenterLanguage.getSelectedItem();
        return item == null ? null : ((Language) item).getID();
    }

    @Override
    public void apply() {
        FluidConfig.setAutoGenerateCloseTagEnabled(myAutoGenerateClosingTagCheckBox.isSelected());
        FluidConfig.setAutocompleteMustachesEnabled(myAutocompleteMustaches.isSelected());
        FluidConfig.setFormattingEnabled(myFormattingCheckBox.isSelected());
        FluidConfig.setCommenterLanguage((Language) myCommenterLanguage.getSelectedItem());

        if (FluidConfig.setShouldOpenHtmlAsHandlebars(htmlAsHb.isSelected(), myProject)) {
            ApplicationManager.getApplication().runWriteAction(() -> FileTypeManagerEx.getInstanceEx().fireFileTypesChanged());
        }
    }

    @Override
    public void reset() {
        myAutoGenerateClosingTagCheckBox.setSelected(FluidConfig.isAutoGenerateCloseTagEnabled());
        myAutocompleteMustaches.setSelected(FluidConfig.isAutocompleteMustachesEnabled());
        myFormattingCheckBox.setSelected(FluidConfig.isFormattingEnabled());
        htmlAsHb.setSelected(FluidConfig.shouldOpenHtmlAsFluid(myProject));
        resetCommentLanguageCombo(FluidConfig.getCommenterLanguage());
    }

    private void resetCommentLanguageCombo(@NotNull Language commentLanguage) {
        final DefaultComboBoxModel model = (DefaultComboBoxModel) myCommenterLanguage.getModel();
        final List<Language> languages = TemplateDataLanguageMappings.getTemplateableLanguages();

        languages.add(FluidLanguage.INSTANCE);

        languages.sort(Comparator.comparing(Language::getID));
        for (Language language: languages) {
            model.addElement(language);
        }

        myCommenterLanguage.setRenderer(new ListCellRendererWrapper() {
            @Override
            public void customize(JList list, Object value, int index, boolean selected, boolean hasFocus) {
                setText(value == null ? "" : ((Language) value).getDisplayName());
                if (value != null) {
                    final FileType type = ((Language) value).getAssociatedFileType();
                    if (type != null) {
                        setIcon(type.getIcon());
                    }
                }
            }
        });
        myCommenterLanguage.setSelectedItem(commentLanguage);
    }
}
