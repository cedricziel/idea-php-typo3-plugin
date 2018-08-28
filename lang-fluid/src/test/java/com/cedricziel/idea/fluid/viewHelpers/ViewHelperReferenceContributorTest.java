package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;

public class ViewHelperReferenceContributorTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/viewHelpers";
    }

    public void testCanPlaceReferencesOnInlineViewHelpers() {
        myFixture.copyFileToProject("classes.php");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.baz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.baz.buzz.fo<caret>o() }");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:baz.buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:bar.baz.buzz.fo<caret>o() }");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:baz.buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:bar.baz.buzz.fo<caret>o() }");
    }
}
