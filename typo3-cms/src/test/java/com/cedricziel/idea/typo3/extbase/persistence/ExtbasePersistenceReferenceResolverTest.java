package com.cedricziel.idea.typo3.extbase.persistence;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.resolve.PhpReferenceResolver;

import java.util.Collection;

public class ExtbasePersistenceReferenceResolverTest extends BasePlatformTestCase {
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

        int caretOffset = myFixture.getCaretOffset();
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(caretOffset).getParent();

        assertInstanceOf(elementAtCaret, MethodReference.class);

        for (PsiReference ref: elementAtCaret.getReferences()) {
            if (ref instanceof PhpReference) {
                for (PhpReferenceResolver resolver: PhpReferenceResolver.EP_NAME.getExtensions()) {
                    Collection<? extends PhpNamedElement> resolve = resolver.resolve((PhpReference) ref);

                    for (PhpNamedElement phpNamedElement: resolve) {
                        if (phpNamedElement.getName().equals("author") && phpNamedElement instanceof Field) {
                            return;
                        }
                    }
                }
            }
        }

        fail("Could not resolve to correct object");
    }
}
