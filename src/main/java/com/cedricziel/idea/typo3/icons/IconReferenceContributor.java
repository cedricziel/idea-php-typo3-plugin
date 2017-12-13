package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class IconReferenceContributor extends PsiReferenceContributor {

    private static final String ICON_FACTORY = "\\TYPO3\\CMS\\Core\\Imaging\\IconFactory";

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

                        PsiElement parent = stringLiteralExpression.getParent();
                        while (!(parent instanceof MethodReference)) {

                            if (parent != null) {
                                parent = parent.getParent();

                                continue;
                            }

                            return new PsiReference[0];
                        }

                        MethodReference methodReference = (MethodReference) parent;
                        String methodName = methodReference.getName();

                        if (methodReference.getFirstPsiChild() instanceof Variable) {
                            Variable variable = (Variable) methodReference.getFirstPsiChild();
                            PhpType inferredType = variable.getInferredType();
                            Set<String> types = inferredType.getTypes();
                            for (String type : types) {
                                try {
                                    Collection<? extends PhpNamedElement> bySignature = PhpIndex.getInstance(element.getProject()).getBySignature(type);
                                    for (PhpNamedElement el : bySignature) {
                                        if (el.getFQN().equals(ICON_FACTORY) && methodName.equals("getIcon")) {
                                            return new PsiReference[]{new IconReference(stringLiteralExpression)};
                                        }
                                    }
                                } catch (RuntimeException e) {
                                    // invalid index signature, skip
                                }
                            }
                        }

                        String className = PhpLangUtil.getClassName(stringLiteralExpression);
                        if (methodName != null && className != null && methodName.equals("getIcon") && className.equals(ICON_FACTORY)) {
                            return new PsiReference[]{new IconReference(stringLiteralExpression)};
                        }

                        return new PsiReference[0];
                    }
                }
        );
    }
}
