package com.cedricziel.idea.fluid.completion.providers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

import java.util.List;

public class FluidTypeCompletionProviderTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/completion";
    }

    public void testAccessorsOnVariablesFromControllerAndTypesAreCompleted() {
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("Index_accessor.fluid", "Book/Index.fluid");

        myFixture.configureByFile("Book/Index.fluid");

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertContainsElements(lookupElementStrings, "author");
    }

    public void testSemiAccessorsOnVariablesFromControllerAndTypesAreCompleted() {
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("Index_accessor_re.fluid", "Book/Index.fluid");

        myFixture.configureByFile("Book/Index.fluid");

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        myFixture.checkResultByFile("Index_accessor_re_after.fluid");
    }
}
