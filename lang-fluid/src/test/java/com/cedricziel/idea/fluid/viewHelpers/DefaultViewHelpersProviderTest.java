package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class DefaultViewHelpersProviderTest extends AbstractFluidTest {
    public void testDefaultViewHelpersAreProvided() {
        myFixture.configureByText("foo.fluid", "{<caret>}");

        myFixture.completeBasic();
        assertCurrentCompletionContains("f:alias");
    }
}
