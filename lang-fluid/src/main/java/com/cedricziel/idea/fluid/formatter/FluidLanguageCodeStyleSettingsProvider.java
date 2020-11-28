package com.cedricziel.idea.fluid.formatter;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;

public class FluidLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    public FluidLanguageCodeStyleSettingsProvider() {
    }

    @NotNull
    public Language getLanguage() {

        return FluidLanguage.INSTANCE;
    }

    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions("RIGHT_MARGIN");
            consumer.showStandardOptions("WRAP_ON_TYPING");
        }
    }

    public String getCodeSample(@NotNull SettingsType settingsType) {

        return "<!DOCTYPE html>\n<html>\n<body>\n<div id=\"content\">{% block content %}{% endblock %}</div>\n<div id=\"footer\">\n\t{% block footer %}\n\t\t&copy; Copyright 2011 by <a\n\t\t\thref=\"http://domain.invalid/\">you</a>.\n\t{% endblock %}\n</div>\n<p>{{ textarea('comment') }}</p>\n</body>\n</html>";
    }

    public IndentOptionsEditor getIndentOptionsEditor() {

        return new SmartIndentOptionsEditor();
    }

    @Override
    @NotNull
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings styleSettings = new CommonCodeStyleSettings(this.getLanguage());
        styleSettings.initIndentOptions();

        return styleSettings;
    }
}
