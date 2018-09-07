package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class ContextReferenceContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/contextApi";
    }

    public void testAContextReferenceIsSet() {
        myFixture.copyFileToProject("classes.php");

        PsiFile psiFile = myFixture.configureByText("foo.php",
            "<?php\n" +
                "/** @var \\TYPO3\\CMS\\Core\\Context\\Context $context */\n" +
                "$context->getAspect('fro<caret>ntend.user');"
        );

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getCaretOffset());

        assertInstanceOf(elementAtCaret.getParent(), StringLiteralExpression.class);

        StringLiteralExpression literalExpression = (StringLiteralExpression) elementAtCaret.getParent();

        assertContainsReference(ContextReference.class, literalExpression);
    }
}
