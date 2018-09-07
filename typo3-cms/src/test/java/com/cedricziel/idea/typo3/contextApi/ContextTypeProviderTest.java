package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public class ContextTypeProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/contextApi";
    }

    public void testContextTypesCanBeInferred() {
        myFixture.copyFileToProject("classes.php");

        PsiFile psiFile = myFixture.configureByText("foo.php", "<?php\n" +
            "$userAspect = \\TYPO3\\CMS\\Core\\Utility\\GeneralUtility::makeInstance(\\TYPO3\\CMS\\Core\\Context\\Context::class)->getAspect('frontend.user');\n" +
            "$userAspect->getGroup<caret>Ids()");


        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getCaretOffset());
        assertNotNull(elementAtCaret);

        assertInstanceOf(elementAtCaret.getParent(), MethodReference.class);

        MethodReference methodReference = (MethodReference) elementAtCaret.getParent();
        Method method = (Method) methodReference.resolve();

        assertNotNull(method);
        assertInstanceOf(method, Method.class);

        assertNotNull(method.getContainingClass());
        assertInstanceOf(method.getContainingClass(), PhpClass.class);
    }
}
