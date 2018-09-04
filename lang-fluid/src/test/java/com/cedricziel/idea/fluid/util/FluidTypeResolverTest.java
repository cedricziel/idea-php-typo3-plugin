package com.cedricziel.idea.fluid.util;

import com.cedricziel.idea.fluid.AbstractFluidTest;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Collection;

public class FluidTypeResolverTest extends AbstractFluidTest {
    public void testCanFormatPsiTypeNameOnVariables() {
        assertResolvesToTypes("{foo.<caret>}", "foo");
        assertResolvesToTypes("{foo.bar.<caret>}", "foo", "bar");
        assertResolvesToTypes("{foo.bar.baz.<caret>}", "foo", "bar", "baz");

        assertResolvesToTypes("{foo.b<caret>}", true, "foo");
        assertResolvesToTypes("{foo.bar.b<caret>}", true, "foo", "bar");
        assertResolvesToTypes("{foo.bar.baz.b<caret>}", true, "foo", "bar", "baz");
    }

    /*
    public void testCanFormatPsiTypeNamesOnVariablesInChains() {
        assertResolvesToTypes("{foo.<caret> -> f:foo()}", "foo");
        assertResolvesToTypes("{foo.bar.<caret> -> f:foo()}", "foo", "bar");
        assertResolvesToTypes("{foo.bar.baz.<caret> -> f:foo()}", "foo", "bar", "baz");

        assertResolvesToTypes("{foo.b<caret> -> f:foo()}", "foo");
        assertResolvesToTypes("{foo.bar.b<caret> -> f:foo()}", "foo", "bar");
        assertResolvesToTypes("{foo.bar.baz.b<caret> -> f:foo()}", "foo", "bar", "baz");
    }
    */

    private void assertResolvesToTypes(String content, String... typeNames) {
        assertResolvesToTypes(content, false, typeNames);
    }

    private void assertResolvesToTypes(String content, boolean cutLastSegment, String... typeNames) {
        PsiFile psiFile = myFixture.configureByText("Foo.fluid", content);

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        Collection<String> resolvedTypes = FluidTypeResolver.formatPsiTypeName(elementAt, cutLastSegment);
        assertSize(typeNames.length, resolvedTypes.toArray());

        for (String typeName : typeNames) {
            assertTrue(resolvedTypes.contains(typeName));
        }
    }
}
