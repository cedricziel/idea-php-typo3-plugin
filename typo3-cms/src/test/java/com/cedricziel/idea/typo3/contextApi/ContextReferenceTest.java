package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.util.TYPO3Utility;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class ContextReferenceTest extends AbstractTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/contextApi";
    }

    public void testAContextReferenceCanReturnVariants() {
        myFixture.copyFileToProject("classes.php");

        PsiFile psiFile = myFixture.configureByText("foo.php",
            "<?php\n" +
                "/** @var \\TYPO3\\CMS\\Core\\Context\\Context $context */\n" +
                "$context->getAspect('<caret>');"
        );

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getCaretOffset());
        assertInstanceOf(elementAtCaret.getParent(), StringLiteralExpression.class);

        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), TYPO3Utility.getAvailableAspects());
    }

    public void testCanResolveToAspectClass() {
        myFixture.copyFileToProject("classes.php");

        PsiFile psiFile = myFixture.configureByText("foo.php",
            "<?php\n" +
                "/** @var \\TYPO3\\CMS\\Core\\Context\\Context $context */\n" +
                "$context->getAspect('frontend.u<caret>ser');"
        );

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getCaretOffset());
        assertInstanceOf(elementAtCaret.getParent(), StringLiteralExpression.class);

        StringLiteralExpression literalExpression = (StringLiteralExpression) elementAtCaret.getParent();
        for (PsiReference reference : literalExpression.getReferences()) {
            if (reference instanceof ContextReference) {
                PsiElement resolve = reference.resolve();

                assertNotNull(resolve);
                assertInstanceOf(resolve, PhpClass.class);

                return;
            }
        }

        fail("No reference found.");
    }
}
