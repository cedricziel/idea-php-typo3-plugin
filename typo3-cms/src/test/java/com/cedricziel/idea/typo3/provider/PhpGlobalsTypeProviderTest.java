package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.MethodReference;

public class PhpGlobalsTypeProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/provider";
    }

    public void testGlobalTypesAreResolved() {
        assertCanResolveGlobalsValue(
            "TYPO3_REQUEST",
            "#M#C\\Psr\\Http\\Message\\ServerRequestInterface.getServerParams",
            "getServer<caret>Params"
        );
        assertCanResolveGlobalsValue(
            "LANG",
            "#M#C\\TYPO3\\CMS\\Lang\\LanguageService.getLL",
            "get<caret>LL"
        );
        assertCanResolveGlobalsValue(
            "TYPO3_DB",
            "#M#C\\TYPO3\\CMS\\Core\\Database\\DatabaseConnection.exec_SELECTgetRows",
            "exec_<caret>SELECTgetRows"
        );
        assertCanResolveGlobalsValue(
            "BE_USER",
            "#M#C\\TYPO3\\CMS\\Core\\Authentication\\BackendUserAuthentication.getPagePermsClause",
            "getPagePerms<caret>Clause"
        );
        assertCanResolveGlobalsValue(
            "TSFE",
            "#M#C\\TYPO3\\CMS\\Frontend\\Controller\\TypoScriptFrontendController.getPageAndRootlineWithDomain",
            "getPageAndRootlineWith<caret>Domain"
        );
    }

    private void assertCanResolveGlobalsValue(String index, String expectedType, String method) {
        PsiFile psiFile = myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "$foo = $GLOBALS['" + index + "'];\n" +
            "$foo->" + method + "();");

        PsiElement elementAtCaret = psiFile.findElementAt(myFixture.getCaretOffset());
        assertNotNull(elementAtCaret);
        assertInstanceOf(elementAtCaret.getParent(), MethodReference.class);

        MethodReference methodReference = (MethodReference) elementAtCaret.getParent();
        assertTrue(methodReference.getType().getTypes().contains(expectedType));
    }
}
