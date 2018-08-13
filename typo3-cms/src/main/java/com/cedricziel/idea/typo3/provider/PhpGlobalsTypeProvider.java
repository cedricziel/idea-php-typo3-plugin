package com.cedricziel.idea.typo3.provider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression;
import com.jetbrains.php.lang.psi.elements.ArrayIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * TypeProvider for `$GLOBALS`
 */
public class PhpGlobalsTypeProvider implements PhpTypeProvider3 {

    @Override
    public char getKey() {
        return '\u0235';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        // $GLOBALS['TYPO3_DB']
        if (!(psiElement instanceof ArrayAccessExpression)) {
            return null;
        }

        VariableImpl variable = PsiTreeUtil.getChildOfType(psiElement, VariableImpl.class);
        if (variable == null || !variable.getName().equals("GLOBALS")) {
            return null;
        }

        ArrayIndex arrayIndex = PsiTreeUtil.getChildOfType(psiElement, ArrayIndex.class);
        if (arrayIndex == null) {
            return null;
        }

        StringLiteralExpression arrayIndexName = PsiTreeUtil.getChildOfType(arrayIndex, StringLiteralExpressionImpl.class);
        if (arrayIndexName == null) {
            return null;
        }

        switch (arrayIndexName.getContents()) {
            case "TYPO3_DB":
                return new PhpType().add("#C\\TYPO3\\CMS\\Core\\Database\\DatabaseConnection");
            case "TSFE":
                return new PhpType().add("#C\\TYPO3\\CMS\\Frontend\\Controller\\TypoScriptFrontendController");
            case "BE_USER":
                return new PhpType().add("#C\\TYPO3\\CMS\\Core\\Authentication\\BackendUserAuthentication");
            case "LANG":
                return new PhpType().add("#C\\TYPO3\\CMS\\Lang\\LanguageService");
            case "TYPO3_REQUEST":
                return new PhpType().add("#C\\Psr\\Http\\Message\\ServerRequestInterface");
            default:
                return null;
        }
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return PhpIndex.getInstance(project).getBySignature(expression);
    }
}
