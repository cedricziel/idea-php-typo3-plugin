package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class PhpViewHelpersProviderTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/viewHelpers/php";
    }

    public void testViewHelpersCanBeProvidedViaPHP() {
        myFixture.copyFileToProject("classes.php");

        configureNamespaceAndTriggerComplete("{namespace a=App\\ViewHelpers}");
        assertCurrentCompletionContains("a:foo");

        configureNamespaceAndTriggerComplete("{namespace app=App\\ViewHelpers}");
        assertCurrentCompletionContains("app:foo");
    }

    private void configureNamespaceAndTriggerComplete(String namespaceNode) {
        myFixture.configureByText(
            "foo.fluid",
            namespaceNode + "\n" + "{<caret>}"
        );

        myFixture.completeBasic();
    }
}
