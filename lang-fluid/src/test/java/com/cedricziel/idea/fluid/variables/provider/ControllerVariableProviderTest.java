package com.cedricziel.idea.fluid.variables.provider;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class ControllerVariableProviderTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/variables/provider/controller";
    }

    public void testThatAControllerVariableCanBeProvidedByActionNameRelation() {
        myFixture.copyFileToProject("SimpleController.php");
        myFixture.copyFileToProject("Overview.fluid", "Simple/Overview.fluid");

        myFixture.configureByFile("Simple/Overview.fluid");

        myFixture.completeBasic();

        assertCurrentCompletionContains("fancy_variable");
        assertCurrentCompletionContains("multi_assign");
    }
}
