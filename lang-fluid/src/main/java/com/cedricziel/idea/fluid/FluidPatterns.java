package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class FluidPatterns {
    /*
     * { f<caret>oo }
     *
     * not
     *
     * { foo.b<caret> }
     */
    public static ElementPattern<PsiElement> getFirstIdentifierPattern() {

        return PlatformPatterns
            .psiElement(FluidTypes.IDENTIFIER)
            .andNot(
                PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(FluidTypes.DOT))
            )
            .andNot(
                PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(FluidInlineChain.class))
            )
            .withLanguage(FluidLanguage.INSTANCE);
    }

    /*
     * {foo.<caret>}
     * {foo.b<caret>}
     */
    public static ElementPattern<PsiElement> getAccessorPattern() {

        return PlatformPatterns
            .or(
                getAccessorAfterDotPattern(),
                getAccessorInIdentifierPattern()
            );
    }

    /*
     * {foo.b<caret>}
     */
    @NotNull
    public static ElementPattern<PsiElement> getAccessorInIdentifierPattern() {
        return PlatformPatterns.psiElement(FluidTypes.IDENTIFIER)
            .afterLeaf(
                PlatformPatterns.psiElement(FluidTypes.DOT)
            )
            .withParent(
                PlatformPatterns.psiElement(FluidFieldChain.class).afterSibling(PlatformPatterns.psiElement(FluidFieldExpr.class))
            )
            .withLanguage(FluidLanguage.INSTANCE);
    }

    /*
     * {foo.<caret>}
     */
    @NotNull
    public static ElementPattern<PsiElement> getAccessorAfterDotPattern() {

        return PlatformPatterns
            .psiElement(FluidTypes.IDENTIFIER)
            .afterLeaf(
                PlatformPatterns.psiElement(FluidTypes.DOT)
            )
            .withLanguage(FluidLanguage.INSTANCE);
    }

    /*
     * {foo -> <caret>}
     */
    @NotNull
    public static ElementPattern<PsiElement> inlineChainPipeTarget() {
        return PlatformPatterns
            .or(
                PlatformPatterns
                    .psiElement(FluidTypes.IDENTIFIER)
                    .afterLeaf("->"),
                PlatformPatterns
                    .psiElement()
                    .withParent(FluidInlineStatement.class),
                PlatformPatterns
                    .psiElement(FluidTypes.IDENTIFIER)
                    .afterSibling(PlatformPatterns.psiElement(FluidChain.class).withFirstChild(
                        PlatformPatterns.psiElement(FluidTypes.ARROW)
                    ))
            );
    }

    public static ElementPattern<? extends PsiElement> inlineArgumentNamePattern() {

        return PlatformPatterns.or(
            /*
             * "{ f:foo(<caret>) }"
             * "{ f:foo(u<caret>) }"
             */
            PlatformPatterns
                .psiElement(FluidTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns.psiElement(FluidFieldExpr.class).afterSibling(
                        PlatformPatterns.psiElement(FluidViewHelperExpr.class)
                    )
                ),
            /* 2018.1 fallback */
            PlatformPatterns
                .psiElement(FluidTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns.psiElement(FluidInlineChain.class).withFirstChild(
                        PlatformPatterns.psiElement(FluidViewHelperExpr.class)
                    )
                ),
            /*
             * "{ f:foo(u<caret>:) }"
             */
            PlatformPatterns
                .psiElement(FluidTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns.psiElement(FluidArgumentKey.class)
                )
        );
    }

    /*
     * {foo<caret>}
     * {foo -> a:bar()<caret>}
     */
    public static ElementPattern<? extends PsiElement> inlinePostfixPositionPattern() {

        return PlatformPatterns
            .or(
                PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(
                    PlatformPatterns.psiElement(FluidFieldExpr.class)
                ),
                PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(
                    PlatformPatterns.psiElement(FluidFieldChain.class)
                ),
                PlatformPatterns.and(
                    PlatformPatterns.psiElement().withParent(
                        FluidViewHelperExpr.class
                    ),
                    PlatformPatterns.psiElement(FluidTypes.RIGHT_PARENTH)
                )
            );
    }
}
