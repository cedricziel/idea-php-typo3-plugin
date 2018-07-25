package com.cedricziel.idea.fluid.lang.parser;

import com.cedricziel.idea.fluid.lang.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class ParserTest extends LightCodeInsightFixtureTestCase {
    public void testCanDetectVariables() {
        assertParentElementAtCaretMatchesType("{fo<caret>o}", FluidFieldChain.class);
        assertParentElementAtCaretMatchesType("{fo<caret>o -> f:fo()}", FluidFieldChain.class);

        assertParentElementAtCaretMatchesType("{foo -> f:fo(fo<caret>o: 'bar')}", FluidArgumentKey.class);
        assertParentElementAtCaretMatchesType("{foo -> f:fo(foo: 'ba<caret>r')}", FluidStringLiteral.class);

        assertParentElementAtCaretMatchesType("{foo -> f:fo(foo: 'ba<caret>r')}", FluidStringLiteral.class);

        assertParentElementAtCaretMatchesType("{foo.ba<caret>r}", FluidFieldChain.class);
    }

    public void testCanDetectViewHelpers() {
        assertParentElementAtCaretMatchesType("{f:f<caret>o()}", FluidViewHelperReference.class);
        assertParentElementAtCaretMatchesType("{foo -> f:f<caret>o()}", FluidViewHelperReference.class);
    }

    public void testCanDetectNamespaceDeclarations() {
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar')}", FluidNamespaceStatement.class);
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar\\Baz')}", FluidNamespaceStatement.class);
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar\\Baz\\Buz')}", FluidNamespaceStatement.class);
    }

    public void testCanDetectExpressions() {
        assertParentElementAtCaretMatchesType("{foo <caret>+ bar)}", FluidAdditiveExpr.class);
        assertParentElementAtCaretMatchesType("{foo <caret>- bar)}", FluidAdditiveExpr.class);

        assertParentElementAtCaretMatchesType("{foo <caret>* bar)}", FluidMultiplicativeExpr.class);
        assertParentElementAtCaretMatchesType("{foo <caret>/ bar)}", FluidMultiplicativeExpr.class);
        assertParentElementAtCaretMatchesType("{foo <caret>% bar)}", FluidMultiplicativeExpr.class);
    }

    public void testCanDetectBooleanLiterals() {
        assertParentElementAtCaretMatchesType("{foo -> f:fo(foo: <caret>true)}", FluidBooleanLiteral.class);
        assertParentElementAtCaretMatchesType("{foo -> f:fo(foo: <caret>false)}", FluidBooleanLiteral.class);
    }

    private void assertParentElementAtCaretMatchesType(String content, Class expected) {
        PsiFile psiFile = myFixture.configureByText("foo.fluid", content);
        int offset = myFixture.getEditor().getCaretModel().getOffset();

        PsiElement elementAt = psiFile.findElementAt(offset);

        assertInstanceOf(elementAt.getParent(), expected);
    }
}
