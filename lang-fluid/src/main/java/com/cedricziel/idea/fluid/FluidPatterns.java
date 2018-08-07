package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.FluidChain;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldChain;
import com.cedricziel.idea.fluid.lang.psi.FluidInlineStatement;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;

public class FluidPatterns {
    /*
     * { f<caret>oo }
     */
    public static ElementPattern<PsiElement> getFirstIdentifierPattern() {

        return PlatformPatterns
            .psiElement()
            .andNot(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(FluidTypes.DOT))
                )
            )
            .afterLeafSkipping(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement(FluidTypes.EXPR_START),
                    PlatformPatterns.psiElement(PsiWhiteSpace.class)
                ),
                PlatformPatterns.psiElement()
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
}
