package com.cedricziel.idea.fluid.completion;

import com.cedricziel.idea.fluid.FluidPatterns;
import com.cedricziel.idea.fluid.completion.providers.FluidTypeCompletionProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidArgumentValue;
import com.cedricziel.idea.fluid.lang.psi.FluidBoundNamespace;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.cedricziel.idea.fluid.util.FluidTypeResolver;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;

public class FluidCompletionContributor extends CompletionContributor {
    public FluidCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                parameters.getPosition();
            }
        });

        /*
         * Initial pattern should complete only if nothing else or an identifier is completed.
         *
         * { <caret> }
         * { fo<caret> }
         */
        extend(CompletionType.BASIC, FluidPatterns.getFirstIdentifierPattern(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement psiElement = parameters.getPosition().getOriginalElement();

                FluidTypeResolver.collectScopeVariables(parameters.getOriginalPosition()).forEach((name, var) -> result.addElement(
                        LookupElementBuilder
                                .create(name)
                                .withTypeText(
                                        FluidTypeResolver.getTypeDisplayName(psiElement.getProject(), var.getTypes()), true
                                )
                                .withIcon(PhpIcons.VARIABLE)
                ));

                if (!PlatformPatterns.psiElement().withSuperParent(2, FluidArgumentValue.class).accepts(psiElement)) {
                    FluidUtil.completeViewHelpers(parameters, result);
                }
            }
        });

        /*
         * {foo.<caret>}
         */
        extend(CompletionType.BASIC, FluidPatterns.getAccessorAfterDotPattern(), new FluidTypeCompletionProvider(false));

        /*
         * {foo.ba<caret>}
         */
        extend(CompletionType.BASIC, FluidPatterns.getAccessorInIdentifierPattern(), new FluidTypeCompletionProvider(true));

        /*
         * {object -> <caret>}
         */
        extend(CompletionType.BASIC, FluidPatterns.inlineChainPipeTarget(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidBoundNamespace.class), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidViewHelperReference.class), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });

        /*
         * {f:foo(<caret>)}
         * {f:foo(u<caret>)}
         * {f:foo(u<caret>:)}
         */
        extend(CompletionType.BASIC, FluidPatterns.inlineArgumentNamePattern(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelperArguments(parameters, result);
            }
        });
    }
}
