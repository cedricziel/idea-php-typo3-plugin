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
     */
    public static ElementPattern<PsiElement> getFirstIdentifierPattern() {

        return PlatformPatterns
            .psiElement(FluidTypes.IDENTIFIER)
            .andNot(
                PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(FluidTypes.DOT))
            )
            .withLanguage(FluidLanguage.INSTANCE);
    }

    /*
     * {foo.b<caret>}
     */
    public static ElementPattern<PsiElement> getAccessorPattern() {

        return PlatformPatterns
            .or(
                PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).afterLeaf(
                    PlatformPatterns.psiElement(FluidTypes.DOT)
                ).withLanguage(FluidLanguage.INSTANCE),
                PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidFieldChain.class).afterLeaf(
                    PlatformPatterns.psiElement(FluidTypes.DOT)
                ).withLanguage(FluidLanguage.INSTANCE)
            );
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

    public static ElementPattern<? extends PsiElement> inlineArgumentName() {

        return PlatformPatterns.or(
            /*
             * "{ f:foo(<caret>) }"
             * "{ f:foo(u<caret>) }"
             */
            PlatformPatterns
                .psiElement(FluidTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns.psiElement(FluidFieldChain.class).afterSibling(PlatformPatterns.psiElement(FluidViewHelperExpr.class))
                ),
            /* 2018.1 fallback */
            PlatformPatterns
                .psiElement(FluidTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns.psiElement(FluidFieldChain.class).withParent(
                        PlatformPatterns.psiElement(FluidFieldChainExpr.class).withFirstChild(
                            PlatformPatterns.psiElement(FluidViewHelperExpr.class)
                        )
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
}
