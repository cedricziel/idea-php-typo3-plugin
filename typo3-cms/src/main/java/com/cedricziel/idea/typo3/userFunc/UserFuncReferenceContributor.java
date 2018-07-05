package com.cedricziel.idea.typo3.userFunc;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class UserFuncReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        /*
         * <userFunc>My\Extension\Foo\Bar->method</userFunc>
         */
        registrar.registerReferenceProvider(
                UserFuncPatterns.expectUserFuncReferenceXMLPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        return new PsiReference[]{new UserFuncReference((XmlText) element.getParent())};
                    }
                }
        );

        /*
         * ['userFunc' => 'My\Extension\Foo\Bar->method']
         */
        registrar.registerReferenceProvider(
                UserFuncPatterns.expectUserFuncReferenceArrayValuePattern("userFunc"),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression element1 = (StringLiteralExpression) element;
                        if (element1.getContents().contains("->")) {
                            int i = element1.getContents().indexOf("->");

                            return new PsiReference[]{
                                    new UserFuncReference(element1, new TextRange(i + 3, element1.getTextLength() - 1))
                            };
                        }

                        return new PsiReference[]{new UserFuncReference(element1)};
                    }
                }
        );

        /*
         * ['itemsProcFunc' => 'My\Extension\Foo\Bar->method']
         */
        registrar.registerReferenceProvider(
                UserFuncPatterns.expectUserFuncReferenceArrayValuePattern("itemsProcFunc"),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression element1 = (StringLiteralExpression) element;
                        if (element1.getContents().contains("->")) {
                            int i = element1.getContents().indexOf("->");

                            return new PsiReference[]{
                                    new UserFuncReference(element1, new TextRange(i + 3, element1.getTextLength() - 1))
                            };
                        }

                        return new PsiReference[]{new UserFuncReference(element1)};
                    }
                }
        );

        /*
         * ['userFunc' => My\Extension\Foo\Bar::class . '->method']
         */
        registrar.registerReferenceProvider(
                UserFuncPatterns.expectUserFuncReferenceStringConcatArrayValuePattern("userFunc"),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        ConcatenationExpression parent = (ConcatenationExpression) element.getParent();
                        StringLiteralExpression element1 = (StringLiteralExpression) element;
                        if (element1.getContents().contains("->")) {
                            int i = element1.getContents().indexOf("->");

                            return new PsiReference[]{
                                    new UserFuncReference(element1, new TextRange(i + 3, element1.getTextLength() - 1), parent)
                            };
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                }
        );

        /*
         * ['itemsProcFunc' => My\Extension\Foo\Bar::class . '->method']
         */
        registrar.registerReferenceProvider(
                UserFuncPatterns.expectUserFuncReferenceStringConcatArrayValuePattern("itemsProcFunc"),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        ConcatenationExpression parent = (ConcatenationExpression) element.getParent();
                        StringLiteralExpression element1 = (StringLiteralExpression) element;
                        if (element1.getContents().contains("->")) {
                            int i = element1.getContents().indexOf("->");

                            return new PsiReference[]{
                                    new UserFuncReference(element1, new TextRange(i + 3, element1.getTextLength() - 1), parent)
                            };
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                }
        );
    }
}
