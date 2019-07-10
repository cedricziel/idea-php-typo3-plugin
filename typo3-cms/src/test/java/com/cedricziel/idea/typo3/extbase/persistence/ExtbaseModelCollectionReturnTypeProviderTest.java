package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;

public class ExtbaseModelCollectionReturnTypeProviderTest extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/extbase/persistence";
    }

    public void testResolvesObjectStoragePropertiesToObjectTypes() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("FieldTypeProvider.php");

        int caretOffset = myFixture.getCaretOffset();
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(caretOffset).getParent();

        assertInstanceOf(elementAtCaret, MethodReference.class);

        MethodReference methodReference = (MethodReference) elementAtCaret;
        Method m = (Method) methodReference.resolve();

        String fqn = m.getContainingClass().getFQN();
        assertTrue(fqn.equals("\\My\\Extension\\Domain\\Model\\Book"));
    }

    public void testResolvesObjectStoragePropertiesToObjectTypesOnGetters() {
        myFixture.copyFileToProject("PersistenceMocks.php");
        myFixture.configureByFile("MethodTypeProvider.php");

        int caretOffset = myFixture.getCaretOffset();
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(caretOffset).getParent();

        assertInstanceOf(elementAtCaret, MethodReference.class);

        MethodReference methodReference = (MethodReference) elementAtCaret;
        Method m = (Method) methodReference.resolve();

        String fqn = m.getContainingClass().getFQN();
        assertTrue(fqn.equals("\\My\\Extension\\Domain\\Model\\Book"));
    }
}
