package com.cedricziel.idea.fluid.completion;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class FluidCompletionContributorTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/completion";
    }

    public void testInlineViewHelpersAreCompleted() {
        myFixture.copyFileToProject("classes.php");

        assertLookupStringOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ <caret> }", "f:alias", "f:base", "f:for");
        assertLookupStringOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ <caret> }", "a:foo");
    }

    public void testInlineViewHelpersAreNotCompletedAtPlacesTheyDontBelong() {
        myFixture.copyFileToProject("classes.php");

        assertLookupStringNotOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ f:translate(<caret>) }", "f:alias", "f:base", "f:for");
        assertLookupStringNotOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ f:translate(<caret>) }", "a:foo");
        assertLookupStringNotOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ f:translate(id: <caret>) }", "f:alias", "f:base", "f:for");
        assertLookupStringNotOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ f:translate(id: <caret>) }", "a:foo");
    }

    public void testInlineArgumentsAreCompleted() {
        myFixture.copyFileToProject("classes.php");

        assertLookupStringOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ a:foo(<caret>) }", "uid");
        assertLookupStringOnFluidCaret("{namespace a=App\\ViewHelpers}\n { a:foo(u<caret>) }", "uid");
        assertLookupStringOnFluidCaret("{namespace a=App\\ViewHelpers}\n{ a:foo(u<caret>:) }", "uid");
    }
}
