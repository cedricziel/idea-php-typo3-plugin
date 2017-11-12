package com.cedricziel.idea.typo3.util;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class ResourceUtil {
    public static boolean isExtResourcePath(PsiElement element) {
        // must be a string element
        if (!PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(StringLiteralExpression.class)).accepts(element)) {
            return false;
        }

        String currentText = element.getParent().getText();

        return element.getProject().getBasePath() != null && currentText.contains("EXT:");
    }
}
