package com.cedricziel.idea.fluid.variables.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class AllVariableProviderTest extends BasePlatformTestCase {
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
