package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class ExtbasePersistenceCompletionContributorTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testCanCompleteExtbaseRepositoryMagicMethods() {
        myFixture.copyFileToProject("PersistenceMocks.php");

        myFixture.configureByFile("RepositoryMagicFindMethodsCompletion.php");

        LookupElement[] lookupElements;
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

    private void assertContainsLookupElementWithText(LookupElement[] lookupElements, @NotNull String title, @NotNull String tailText, @NotNull String typeText) {
        for (LookupElement lookupElement : lookupElements) {
            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);
            if (presentation.getItemText().equals(title) && presentation.getTailText().equals(tailText) && presentation.getTypeText().contains(typeText)) {
                return;
            }
        }

        fail("No such element");
    }

    private void assertNotContainsLookupElementWithText(LookupElement[] lookupElements, @NotNull String title) {
        for (LookupElement lookupElement : lookupElements) {
            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);
            if (presentation.getItemText().equals(title)) {
                fail("Element shouldnt be present but is");
            }
        }
    }
}
