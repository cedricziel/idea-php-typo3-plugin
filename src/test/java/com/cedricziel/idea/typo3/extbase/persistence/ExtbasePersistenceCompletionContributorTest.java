package com.cedricziel.idea.typo3.extbase.persistence;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.lookup.LookupElement;

public class ExtbasePersistenceCompletionContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testCanCompleteExtbaseRepositoryMagicMethods() {
        myFixture.copyFileToProject("PersistenceMocks.php");

        LookupElement[] lookupElements;

        myFixture.configureByFile("RepositoryMagicFindMethodsCompletion.php");

        lookupElements = myFixture.completeBasic();

        assertContainsLookupElementWithText(lookupElements, "findByTitle", "(title : string)", "Book[]");
        assertContainsLookupElementWithText(lookupElements, "findOneByTitle", "(title : string)", "Book");

        assertContainsLookupElementWithText(lookupElements, "findByAuthor", "(author : string)", "Book[]");
        assertContainsLookupElementWithText(lookupElements, "findOneByAuthor", "(author : string)", "Book");

        assertNotContainsLookupElementWithText(lookupElements, "findByPublishers");
        assertNotContainsLookupElementWithText(lookupElements, "findOneByPublishers");

        myFixture.configureByFile("RepositoryMagicCountMethodsCompletion.php");

        lookupElements = myFixture.completeBasic();

        assertContainsLookupElementWithText(lookupElements, "countByTitle", "(title : string)", "int");
        assertContainsLookupElementWithText(lookupElements, "countByAuthor", "(author : string)", "int");
        assertNotContainsLookupElementWithText(lookupElements, "countByPublishers");
    }

    public void testCanCompleteExtbaseRepositoryMagicMethodsOnMembers() {
        myFixture.copyFileToProject("PersistenceMocks.php");

        LookupElement[] lookupElements;

        myFixture.configureByFile("RepositoryMagicFindMethodsCompletionOnMember.php");

        lookupElements = myFixture.completeBasic();

        assertContainsLookupElementWithText(lookupElements, "findByTitle", "(title : string)", "Book[]");
        assertContainsLookupElementWithText(lookupElements, "findOneByTitle", "(title : string)", "Book");

        assertContainsLookupElementWithText(lookupElements, "findByAuthor", "(author : string)", "Book[]");
        assertContainsLookupElementWithText(lookupElements, "findOneByAuthor", "(author : string)", "Book");

        assertNotContainsLookupElementWithText(lookupElements, "findByPublishers");
        assertNotContainsLookupElementWithText(lookupElements, "findOneByPublishers");

        myFixture.configureByFile("RepositoryMagicCountMethodsCompletionOnMember.php");

        lookupElements = myFixture.completeBasic();

        assertContainsLookupElementWithText(lookupElements, "countByTitle", "(title : string)", "int");
        assertContainsLookupElementWithText(lookupElements, "countByAuthor", "(author : string)", "int");
        assertNotContainsLookupElementWithText(lookupElements, "countByPublishers");
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
