package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.AbstractFluidTest;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

public class ViewHelperReferenceContributorTest extends AbstractFluidTest {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/viewHelpers";
    }

    public void testCanPlaceReferencesOnInlineViewHelpers() {
        myFixture.copyFileToProject("classes.php");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.baz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ f:bar.baz.buzz.fo<caret>o() }");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:baz.buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:bar.baz.buzz.fo<caret>o() }");

        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:baz.buzz.fo<caret>o() }");
        assertViewHelperReferenceOnCaret("{namespace a=App\\ViewHelpers}\n{ foo -> f:foo() -> f:bar.baz.buzz.fo<caret>o() }");
    }

    public void testCanResolveViewHelperClass() {
        myFixture.copyFileToProject("classes.php");

        assertCanResolveReference("{namespace a=App\\ViewHelpers}\n{ a:fo<caret>o() }");
        assertCanResolveReference("{namespace bar=App\\ViewHelpers}\n{ myVar -> bar:fo<caret>o() }");
    }

    private void assertCanResolveReference(@NotNull String content) {
        PsiFile psiFile = myFixture.configureByText("foo.fluid", content);

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getEditor().getCaretModel().getOffset());
        FluidViewHelperReference viewHelperExpr = (FluidViewHelperReference) PsiTreeUtil.findFirstParent(elementAtCaret, e -> e instanceof FluidViewHelperReference);
        PsiReference[] references = viewHelperExpr.getReferences();
        for (PsiReference reference : references) {
            if (reference instanceof ViewHelperReference) {
                PsiElement resolve = reference.resolve();
                if (resolve instanceof PhpClass) {

                    return;
                }

                fail("Could not resolve element");
            }
        }

    }
}
