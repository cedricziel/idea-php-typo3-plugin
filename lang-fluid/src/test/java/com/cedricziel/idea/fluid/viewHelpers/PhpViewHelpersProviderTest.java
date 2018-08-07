package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class PhpViewHelpersProviderTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/viewHelpers/php";
    }

    public void testViewHelpersCanBeProvidedViaPHP() {
        myFixture.copyFileToProject("classes.php");

        myFixture.configureByText(
            "foo.fluid",
            "{namespace a=App\\ViewHelpers}\n" +
                "{<caret>}"
        );

        myFixture.completeBasic();

        assertCurrentCompletionContains("a:foo");
    }
}
