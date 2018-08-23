package com.cedricziel.idea.fluid.completion;

import com.cedricziel.idea.fluid.AbstractFluidTest;
import com.cedricziel.idea.fluid.lang.FluidFileType;

public class FluidCompletionContributorTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/completion";
    }

    public void testInlineViewHelpersAreCompleted() {
        myFixture.copyFileToProject("classes.php");

        myFixture.configureByText(
            FluidFileType.INSTANCE,
            "{namespace a=App\\ViewHelpers}\n" +
                "{ <caret> }"
        );

        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "f:alias", "f:base", "f:for");
        assertContainsElements(myFixture.getLookupElementStrings(), "a:foo");
    }

    public void testInlineArgumentsAreCompleted() {
        myFixture.copyFileToProject("classes.php");

        myFixture.configureByText(
            FluidFileType.INSTANCE,
            "{namespace a=App\\ViewHelpers}\n" +
                "{ a:foo(<caret>) }"
        );

        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "uid");

        myFixture.configureByText(
            FluidFileType.INSTANCE,
            "{namespace a=App\\ViewHelpers}\n" +
                "{ a:foo(u<caret>) }"
        );

        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "uid");

        myFixture.configureByText(
            FluidFileType.INSTANCE,
            "{namespace a=App\\ViewHelpers}\n" +
                "{ a:foo(u<caret>:) }"
        );

        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "uid");
    }
}
