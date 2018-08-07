package com.cedricziel.idea.fluid.variables;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

public class InTemplateDeclarationVariableProviderTest extends LightCodeInsightFixtureTestCase {
    public void testVariablesAreProvided() {
        assertCompletionContains("<f:variable name=\"foo\" value=\"foo\"/>\n{<caret>}", "foo", "bar");
        assertCompletionContains("{f:variable(name: 'buz', value: 'buz')}", "buz");
        assertCompletionContains("{expr -> f:variable(name: 'piped')}", "piped");
    }

    private void assertCompletionContains(String content, String... completions) {
        myFixture.configureByText("foo.fluid", content);

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        for (String completion : completions) {
            assertTrue(lookupElementStrings.contains(completion));
        }
    }
}
