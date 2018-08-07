package com.cedricziel.idea.fluid.variables.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

public class AllVariableProviderTest extends LightCodeInsightFixtureTestCase {
    public void testAllVariableIsInCompletion() {
        myFixture.configureByText(
            "foo.fluid",
                "{<caret>}"
        );

        LookupElement[] lookupElements = myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue(lookupElementStrings.contains("_all"));
    }
}
