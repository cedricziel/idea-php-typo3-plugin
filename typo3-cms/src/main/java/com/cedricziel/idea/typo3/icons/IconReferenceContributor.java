package com.cedricziel.idea.typo3.icons;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class IconReferenceContributor extends PsiReferenceContributor {

    private static final String ICON_FACTORY = "\\TYPO3\\CMS\\Core\\Imaging\\IconFactory";

    private static ElementPattern<StringLiteralExpression> methodCallOnVariableWithStringArgument() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withSuperParent(
            2,
            PlatformPatterns.psiElement(MethodReference.class).withFirstChild(
                PlatformPatterns.psiElement(Variable.class)
            )
        );
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // known method calls
        registrar.registerReferenceProvider(
            methodCallOnVariableWithStringArgument(),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    StringLiteralExpression stringLiteralExpression = (StringLiteralExpression) element;
                    MethodReference methodReference = (MethodReference) PsiTreeUtil.findFirstParent(stringLiteralExpression, p -> p instanceof MethodReference);

                    if (methodReference == null) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    String methodName = methodReference.getName();
                    if (methodName == null || !methodName.equals("getIcon")) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    String signature = methodReference.getSignature();
                    try {
                        final PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
                        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature(signature);
                        for (PhpNamedElement el : bySignature) {
                            if (el instanceof Method) {
                                String fqn = ((PhpClass) el.getParent()).getFQN();
                                if (fqn.equals(ICON_FACTORY)) {
                                    return new PsiReference[]{new IconReference(stringLiteralExpression)};
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        // invalid index signature, skip
                    }

                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );
    }
}
