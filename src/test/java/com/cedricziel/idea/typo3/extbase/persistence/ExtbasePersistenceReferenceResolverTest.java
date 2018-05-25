package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.psi.elements.Field;

public class ExtbasePersistenceReferenceResolverTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testCanNavigateToPropertiesFromMagicMethods() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("RepositoryMagicMethodNavigation.php");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();

        assertInstanceOf(elementAtCaret, Field.class);
        assertEquals("author", ((Field) elementAtCaret).getName());
    }

    public void testCanNavigateToPropertiesFromMagicMethodsOnMembers() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("RepositoryMagicMethodNavigationOnMember.php");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();

        assertInstanceOf(elementAtCaret, Field.class);
        assertEquals("author", ((Field) elementAtCaret).getName());
    }
}
