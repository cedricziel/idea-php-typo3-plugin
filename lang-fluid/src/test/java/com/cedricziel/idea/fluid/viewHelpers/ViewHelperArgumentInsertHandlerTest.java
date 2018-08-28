package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class ViewHelperArgumentInsertHandlerTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/completion";
    }

    public void testInlineArgumentIsCorrectlyInserted() {
        myFixture.copyFileToProject("classes.php");

        myFixture.configureByText("foo.fluid", "{namespace a=App\\ViewHelpers}\n{ a:foo(ui<caret>) }");
        myFixture.completeBasic();

        assertEquals("{namespace a=App\\ViewHelpers}\n{ a:foo(uid: ) }", myFixture.getFile().getText());
        assertEquals(43, myFixture.getCaretOffset());
    }
}
