package com.cedricziel.idea.fluid.variables.provider;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class InTemplateDeclarationVariableProviderTest extends AbstractFluidTest {
    public void testVariablesAreProvided() {
        assertCompletionContains("<f:variable name=\"foo\" value=\"foo\"/>\n{<caret>}", "foo", "bar");
        assertCompletionContains("{f:variable(name: 'buz', value: 'buz')}", "buz");
        assertCompletionContains("{expr -> f:variable(name: 'piped')}", "piped");
    }
}
