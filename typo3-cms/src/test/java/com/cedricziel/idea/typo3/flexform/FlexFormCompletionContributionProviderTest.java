package com.cedricziel.idea.typo3.flexform;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.completion.CompletionType;

public class FlexFormCompletionContributionProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/flexform";
    }

    public void testFlexFormTableNamesAreCompleted() {
        myFixture.copyFileToProject("ext_tables.sql");
        myFixture.configureByFile("flexform_tablename.xml");

        assertContainsLookupElementWithText(myFixture.complete(CompletionType.BASIC), "tx_myext_domain_model_foo");
    }

    public void testFlexFormFieldTypesAreCompleted() {
        myFixture.copyFileToProject("ext_tables.sql");
        myFixture.configureByFile("flexform_type.xml");

        assertContainsLookupElementWithText(myFixture.complete(CompletionType.BASIC), "select");
    }

    public void testFlexFormFieldRenderTypesAreCompleted() {
        myFixture.copyFileToProject("ext_tables.sql");
        myFixture.configureByFile("flexform_rendertype.xml");

        assertContainsLookupElementWithText(myFixture.complete(CompletionType.BASIC), "selectSingle");
    }
}
