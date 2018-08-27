package com.cedricziel.idea.fluid.lang.parser;

import com.cedricziel.idea.fluid.lang.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class ParserTest extends LightCodeInsightFixtureTestCase {
    public void testCanDetectVariables() {
        assertParentElementAtCaretMatchesType("{fo<caret>o}", FluidFieldExpr.class);
        assertParentElementAtCaretMatchesType("{fo<caret>o -> f:fo()}", FluidFieldExpr.class);

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
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar}", FluidNamespaceStatement.class);
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar\\Baz}", FluidNamespaceStatement.class);
        assertParentElementAtCaretMatchesType("{name<caret>space foo=Bar\\Baz\\Buz}", FluidNamespaceStatement.class);

        assertNamespaceStatementWithPrefixAndNamespace("{namespace f=Bar\\Foo\\Bar}", "f", "Bar/Foo/Bar");
        assertNamespaceStatementWithPrefixAndNamespace("{namespace v=FluidTYPO3\\Vhs\\ViewHelpers}", "v", "FluidTYPO3/Vhs/ViewHelpers");
    }

    private void assertNamespaceStatementWithPrefixAndNamespace(String content, String f, String expected) {
        FluidNamespaceStatement namespaceStatement = (FluidNamespaceStatement) getParentElementAtConfiguredOffset(content, 1);
        assertNotNull(namespaceStatement);
        assertInstanceOf(namespaceStatement, FluidNamespaceStatement.class);
        assertEquals(f, namespaceStatement.getAlias());
        assertEquals(expected, namespaceStatement.getNamespace());
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

    public void testCanParseArrays() {
        // Array Keys are discoverable
        assertParentElementAtCaretMatchesType("{fo<caret>o : bar}", FluidArrayKey.class);
        assertParentElementAtCaretMatchesType("{'fo<caret>o' : bar}", FluidArrayKey.class, 2);
        assertParentElementAtCaretMatchesType("{\"fo<caret>o\" : bar}", FluidArrayKey.class, 2);

        // keys can be string literals
        assertParentElementAtCaretMatchesType("{'fo<caret>o' : bar}", FluidStringLiteral.class);
        assertParentElementAtCaretMatchesType("{\"fo<caret>o\" : bar}", FluidStringLiteral.class);

        // array values can be literals or field chains
        assertParentElementAtCaretMatchesType("{foo : b<caret>ar}", FluidFieldExpr.class);
        assertParentElementAtCaretMatchesType("{foo : b<caret>ar}", FluidArrayCreationExpr.class, 3);
        assertParentElementAtCaretMatchesType("{foo : 'b<caret>ar'}", FluidArrayValue.class, 2);
        assertParentElementAtCaretMatchesType("{foo : \"b<caret>ar\"}", FluidArrayValue.class, 2);
    }

    private void assertParentElementAtCaretMatchesType(String content, Class expected) {
        assertParentElementAtCaretMatchesType(content, expected, 1);
    }

    private void assertParentElementAtCaretMatchesType(String content, Class expected, int levelsUp) {
        assertInstanceOf(getParentElementAtConfiguredOffset(content, levelsUp), expected);
    }

    private PsiElement getParentElementAtConfiguredOffset(String content, int levelsUp) {
        PsiFile psiFile = myFixture.configureByText("foo.fluid", content);
        int offset = myFixture.getEditor().getCaretModel().getOffset();

        PsiElement elementAt = psiFile.findElementAt(offset);
        for (int i = 0; i < levelsUp; i++) {
            elementAt = elementAt.getParent();
        }

        return elementAt;
    }
}
