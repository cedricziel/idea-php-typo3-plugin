package com.cedricziel.idea.fluid.completion;

import com.cedricziel.idea.fluid.FluidViewHelperReferenceInsertHandler;
import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class FluidCompletionContributor extends CompletionContributor {
    public FluidCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                parameters.getPosition();
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidIdentifierExpr.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (VariableProvider extension : VariableProvider.EP_NAME.getExtensions()) {
                    extension.provide(parameters, context).forEach(v -> result.addElement(LookupElementBuilder.create(v.identifier).withIcon(v.icon).withTypeText(v.description)));
                }
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidIdentifierExpr.class).afterLeaf("->"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
                    for (FluidNamespace fluidNamespace : extension.provideForElement(parameters.getPosition())) {
                        for (ViewHelperProvider viewHelperProvider : ViewHelperProvider.EP_NAME.getExtensions()) {
                            viewHelperProvider
                                .provideForNamespace(parameters.getPosition().getProject(), fluidNamespace.namespace)
                                .forEach((name, vh) -> result.addElement(
                                    LookupElementBuilder
                                        .create(fluidNamespace.prefix + ":" + vh.name)
                                        .withIcon(vh.icon)
                                        .withTypeText(vh.fqn)
                                        .withInsertHandler(FluidViewHelperReferenceInsertHandler.INSTANCE)
                                        .withPsiElement(parameters.getPosition())
                                ));
                        }
                    }
                }
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidBoundNamespace.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (ViewHelperProvider extension : ViewHelperProvider.EP_NAME.getExtensions()) {
                    extension.provideForNamespace(parameters.getPosition().getProject(), "")
                        .forEach((name, vh) -> result.addElement(
                            LookupElementBuilder
                                .create(vh.name)
                                .withIcon(vh.icon)
                                .withTypeText(vh.fqn)
                                .withInsertHandler(FluidViewHelperReferenceInsertHandler.INSTANCE)
                                .withPsiElement(parameters.getPosition())
                        ));
                }
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidViewHelperReference.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (ViewHelperProvider extension : ViewHelperProvider.EP_NAME.getExtensions()) {
                    extension.provideForNamespace(parameters.getPosition().getProject(), "")
                        .forEach((name, vh) -> result.addElement(
                            LookupElementBuilder
                                .create(vh.name)
                                .withIcon(vh.icon)
                                .withTypeText(vh.fqn)
                                .withInsertHandler(FluidViewHelperReferenceInsertHandler.INSTANCE)
                                .withPsiElement(parameters.getPosition())
                        ));
                }
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidViewHelperReference.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                for (ViewHelperProvider extension : ViewHelperProvider.EP_NAME.getExtensions()) {
                    extension.provideForNamespace(parameters.getPosition().getProject(), "")
                        .forEach((name, vh) -> result.addElement(
                            LookupElementBuilder
                                .create(vh.name)
                                .withIcon(vh.icon)
                                .withTypeText(vh.fqn)
                                .withInsertHandler(FluidViewHelperReferenceInsertHandler.INSTANCE)
                                .withPsiElement(parameters.getPosition())
                        ));
                }
            }
        });
    }
}
