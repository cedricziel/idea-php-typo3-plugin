package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.util.TableUtil;

public class TablenameIndexTest extends AbstractTestCase {
    public void testTablenameIsExtractedCorrectly() {
        myFixture.configureByText("ext_tables.sql", "CREATE TABLE foo {}");
        assertContainsLookupElementWithText(
            TableUtil.createAvailableTableNamesLookupElements(myFixture.getProject()),
            "foo"
        );

        myFixture.configureByText("ext_tables.sql", "CREATE TABLE `bar` {}");
        assertContainsLookupElementWithText(
            TableUtil.createAvailableTableNamesLookupElements(myFixture.getProject()),
            "bar"
        );

        myFixture.configureByText("ext_tables.sql", "CREATE TABLE `bingo {}");
        assertNotContainsLookupElementWithText(
            TableUtil.createAvailableTableNamesLookupElements(myFixture.getProject()),
            "bingo"
        );
        assertNotContainsLookupElementWithText(
            TableUtil.createAvailableTableNamesLookupElements(myFixture.getProject()),
            "`bingo"
        );

        myFixture.configureByText("ext_tables.sql", "CREATE TABLE bongo` {}");
        assertNotContainsLookupElementWithText(
            TableUtil.createAvailableTableNamesLookupElements(myFixture.getProject()),
            "bongo`"
        );
    }
}
