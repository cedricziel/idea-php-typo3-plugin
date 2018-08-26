package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract public class AbstractFluidTest extends LightCodeInsightFixtureTestCase {
    protected static void assertContainsNamespace(List<FluidNamespace> namespaces, String prefix, String namespace) {
        for (FluidNamespace fluidNamespace : namespaces) {
            if (fluidNamespace.prefix.equals(prefix) && fluidNamespace.namespace.equals(namespace)) {
                return;
            }
        }

        fail(String.format("Expected %s bound to %s, but didnt find it.", namespace, prefix));
    }

    protected void assertCompletionInFileContains(String fileName, String content, String... completions) {
        myFixture.configureByText(fileName, content);

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        for (String completion : completions) {
            assertTrue(lookupElementStrings.contains(completion));
        }
    }

    protected void assertCurrentCompletionContains(String... completions) {
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        for (String completion : completions) {
            assertTrue(lookupElementStrings.contains(completion));
        }
    }

    protected void assertCompletionContains(String content, String... completions) {
        myFixture.configureByText("foo.fluid", content);

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        for (String completion : completions) {
            assertTrue(lookupElementStrings.contains(completion));
        }
    }

    protected void assertLookupStringOnFluidCaret(@NotNull String content, @NotNull String... lookupString) {
        assertLookupStringOnCaret("foo.fluid", content, lookupString);
    }

    protected void assertLookupStringOnCaret(@NotNull String fileName, @NotNull String content, @NotNull String... lookupString) {
        myFixture.configureByText(fileName, content);

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, lookupString);
    }
}
