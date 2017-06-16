package com.cedricziel.idea.typo3.provider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * TypeProvider for `$GLOBALS`
 */
public class PhpGlobalsTypeProvider implements PhpTypeProvider2 {

    @Override
    public char getKey() {
        return '\u0235';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
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
                return "#C\\TYPO3\\CMS\\Core\\Database\\DatabaseConnection";
            case "TSFE":
                return "#C\\TYPO3\\CMS\\Frontend\\Controller\\TypoScriptFrontendController";
            case "BE_USER":
                return "#C\\TYPO3\\CMS\\Core\\Authentication\\BackendUserAuthentication";
            default:
                return null;
        }
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        return PhpIndex.getInstance(project).getBySignature(s);
    }
}
