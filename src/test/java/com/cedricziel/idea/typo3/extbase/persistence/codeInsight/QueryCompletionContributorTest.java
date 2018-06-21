package com.cedricziel.idea.typo3.extbase.persistence.codeInsight;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.lookup.LookupElement;

public class QueryCompletionContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testCanCompleteExtbaseDomainModelFieldsInQuery() {
        myFixture.copyFileToProject("PersistenceMocks.php");

        LookupElement[] lookupElements;

        myFixture.configureByText(
                "foo.php",
                "<?php\n" +
                        "namespace My\\Extension\\Domain\\Repository {\n" +
                        "    class BookRepository extends \\TYPO3\\CMS\\Extbase\\Persistence\\Repository {\n" +
                        "        public function fooBar() {\n" +
                        "            /** @var \\TYPO3\\CMS\\Extbase\\Persistence\\QueryInterface $q */" +
                        "            $q = $this->createQuery();\n" +
                        "            $q->matching(\n" +
                        "                $q->equals('<caret>')\n" +
                        "            );\n" +
                        "        }\n" +
                        "    }\n" +
                        "}"
        );

        lookupElements = myFixture.completeBasic();

        assertContainsLookupElementWithText(lookupElements, "author");
        assertContainsLookupElementWithText(lookupElements, "uid");
        assertContainsLookupElementWithText(lookupElements, "pid");
        assertNotContainsLookupElementWithText(lookupElements, "_cleanProperties");
        assertNotContainsLookupElementWithText(lookupElements, "_isClone");
        assertContainsLookupElementWithText(lookupElements, "_versionedUid");
        assertContainsLookupElementWithText(lookupElements, "_languageUid");
        assertContainsLookupElementWithText(lookupElements, "_localizedUid");
    }
}
