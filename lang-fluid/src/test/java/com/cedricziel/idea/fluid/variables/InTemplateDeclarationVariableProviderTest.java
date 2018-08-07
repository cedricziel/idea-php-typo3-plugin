package com.cedricziel.idea.fluid.variables;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

public class InTemplateDeclarationVariableProviderTest extends LightCodeInsightFixtureTestCase {
    public void testVariablesAreProvided() {
        myFixture.configureByText(
            "foo.fluid",
            "<f:variable name=\"foo\" value=\"foo\"/>\n" +
                "{ f<caret> }"
        );

        LookupElement[] lookupElements = myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue(lookupElementStrings.contains("foo"));
    }
}
