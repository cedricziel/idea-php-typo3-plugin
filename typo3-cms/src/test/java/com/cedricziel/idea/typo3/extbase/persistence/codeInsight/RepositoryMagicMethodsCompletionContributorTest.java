package com.cedricziel.idea.typo3.extbase.persistence.codeInsight;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.codeInsight.lookup.LookupElement;

public class RepositoryMagicMethodsCompletionContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testCompletionIsNotAvailableIfPluginDisabled() {
        disablePlugin();

        myFixture.copyFileToProject("PersistenceMocks.php");

        myFixture.configureByFile("RepositoryMagicFindMethodsCompletion.php");

        assertNotContainsLookupElementWithText(myFixture.completeBasic(), "findByTitle");
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
}
