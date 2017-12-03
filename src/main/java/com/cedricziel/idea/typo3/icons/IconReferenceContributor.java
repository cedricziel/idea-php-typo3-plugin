package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class IconReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // known method calls
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;

                        String methodName = PhpLangUtil.getMethodName(stringLiteralExpression);
                        String className = PhpLangUtil.getClassName(stringLiteralExpression);
                        if (methodName != null && className != null && methodName.equals("getIcon") && className.equals("\\TYPO3\\CMS\\Core\\Imaging\\IconFactory")) {
                            return new PsiReference[]{new IconReference(stringLiteralExpression)};
                        }

                        return new PsiReference[0];
                    }
                }
        );
    }
}
