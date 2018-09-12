package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.viewHelpers.ViewHelperReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract public class AbstractFluidTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid";
    }

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

    protected void assertLookupStringNotOnFluidCaret(@NotNull String content, @NotNull String... lookupString) {
        assertLookupStringNotOnCaret("foo.fluid", content, lookupString);
    }

    protected void assertLookupStringNotOnCaret(@NotNull String fileName, @NotNull String content, @NotNull String... lookupString) {
        myFixture.configureByText(fileName, content);

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        assertNotContainsElements(lookupElementStrings, lookupString);
    }

    protected void assertNotContainsElements(List<String> haystack, String... needles) {
        for (String needle : needles) {
            assertFalse(haystack.contains(needle));
        }
    }

    protected void assertViewHelperReferenceOnCaret(String s) {
        PsiFile psiFile = myFixture.configureByText("foo.fluid", s);

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getEditor().getCaretModel().getOffset());
        FluidViewHelperReference viewHelperReference = (FluidViewHelperReference) PsiTreeUtil.findFirstParent(elementAtCaret, e -> e instanceof FluidViewHelperReference);
        PsiReference[] references = viewHelperReference.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof ViewHelperReference) {
                return;
            }
        }

        fail("No ViewHelperReference found on caret.");
    }
}
