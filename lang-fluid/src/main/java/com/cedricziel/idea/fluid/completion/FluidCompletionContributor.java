package com.cedricziel.idea.fluid.completion;

import com.cedricziel.idea.fluid.FluidPatterns;
import com.cedricziel.idea.fluid.lang.psi.FluidBoundNamespace;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.cedricziel.idea.fluid.lang.psi.FluidViewHelperReference;
import com.cedricziel.idea.fluid.util.FluidTypeResolver;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.cedricziel.idea.fluid.variables.FluidTypeContainer;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.cedricziel.idea.fluid.variables.PhpFluidMethodLookupElement;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class FluidCompletionContributor extends CompletionContributor {
    public FluidCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                parameters.getPosition();
            }
        });

        extend(CompletionType.BASIC, FluidPatterns.getFirstIdentifierPattern(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement psiElement = parameters.getPosition().getOriginalElement();

                for (Map.Entry<String, FluidVariable> entry : FluidTypeResolver.collectScopeVariables(parameters.getOriginalPosition()).entrySet()) {
                    result.addElement(
                        LookupElementBuilder
                            .create(entry.getKey())
                            .withTypeText(
                                FluidTypeResolver.getTypeDisplayName(
                                    psiElement.getProject(),
                                    entry.getValue().getTypes()),
                                true
                            )
                            .withIcon(PhpIcons.CLASS)
                    );
                }
            }
        });

        extend(CompletionType.BASIC, FluidPatterns.getAccessorPattern(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement psiElement = parameters.getOriginalPosition();
                if (psiElement == null) {
                    return;
                }

                Collection<String> possibleTypes = FluidTypeResolver.formatPsiTypeName(psiElement);
                for (FluidTypeContainer fluidTypeContainer : FluidTypeResolver.resolveFluidMethodName(psiElement, possibleTypes)) {
                    if (fluidTypeContainer.getPhpNamedElement() instanceof PhpClass) {

                        for (Method method : ((PhpClass) fluidTypeContainer.getPhpNamedElement()).getMethods()) {
                            if (!(!method.getModifier().isPublic() || method.getName().startsWith("set") || method.getName().startsWith("__"))) {
                                result.addElement(new PhpFluidMethodLookupElement(method));
                            }
                        }

                        for (Field field : ((PhpClass) fluidTypeContainer.getPhpNamedElement()).getFields()) {
                            if (field.getModifier().isPublic()) {
                                result.addElement(new PhpFluidMethodLookupElement(field));
                            }
                        }
                    }

                    if (fluidTypeContainer.getStringElement() != null) {
                        result.addElement(LookupElementBuilder.create(fluidTypeContainer.getStringElement()));
                    }
                }
            }
        });

        extend(CompletionType.BASIC, FluidPatterns.inlineChainPipeTarget(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidBoundNamespace.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(FluidTypes.IDENTIFIER).withParent(FluidViewHelperReference.class), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                FluidUtil.completeViewHelpers(parameters, result);
            }
        });
    }

}
