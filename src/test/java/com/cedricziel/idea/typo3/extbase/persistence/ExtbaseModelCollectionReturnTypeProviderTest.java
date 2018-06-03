package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;

import java.util.Set;

public class ExtbaseModelCollectionReturnTypeProviderTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testResolvesObjectStoragePropertiesToObjectTypes() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("FieldTypeProvider.php");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();

        assertInstanceOf(elementAtCaret, Field.class);

        Set<String> types = ((Field) elementAtCaret).getInferredType().getTypes();
        assertTrue(types.contains("\\My\\Extension\\Domain\\Model\\Book[]"));
    }

    public void testResolvesObjectStoragePropertiesToObjectTypesOnGetters() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("MethodTypeProvider.php");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();

        assertInstanceOf(elementAtCaret, Method.class);

        Set<String> types = ((Method) elementAtCaret).getInferredType().getTypes();
        assertTrue(types.contains("\\My\\Extension\\Domain\\Model\\Book[]"));
    }
}
