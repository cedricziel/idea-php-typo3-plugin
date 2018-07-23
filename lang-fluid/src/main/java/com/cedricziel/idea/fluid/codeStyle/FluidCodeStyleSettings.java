package com.cedricziel.idea.fluid.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class FluidCodeStyleSettings extends CustomCodeStyleSettings {

    public boolean SPACES_INSIDE_DELIMITERS = true;
    public boolean SPACES_INSIDE_VARIABLE_DELIMITERS = true;

    public FluidCodeStyleSettings(CodeStyleSettings settings) {
        super("FluidCodeStyleSettings", settings);
    }
}
