package com.cedricziel.idea.fluid.formatter;

import com.cedricziel.idea.fluid.codeStyle.FluidCodeStyleSettings;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new FluidCodeStyleSettings(settings);
    }

    @Nullable
    @Override
    public String getConfigurableDisplayName() {
        return "Fluid";
    }

    @NotNull
    @Override
    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        return new CodeStyleAbstractConfigurable(settings, originalSettings, "Fluid") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new FluidCodeStyleMainPanel(getCurrentSettings(), settings);
            }

            @Nullable
            @Override
            public String getHelpTopic() {
                return null;
            }
        };
    }

    private static class FluidCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
        public FluidCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
            super(FluidLanguage.INSTANCE, currentSettings, settings);
        }
    }
}
