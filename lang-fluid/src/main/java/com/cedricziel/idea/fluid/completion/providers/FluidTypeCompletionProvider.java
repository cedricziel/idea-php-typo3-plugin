package com.cedricziel.idea.fluid.completion.providers;

import com.cedricziel.idea.fluid.util.FluidTypeResolver;
import com.cedricziel.idea.fluid.variables.FluidTypeContainer;
import com.cedricziel.idea.fluid.variables.PhpFluidMethodLookupElement;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FluidTypeCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final boolean cutLastSegment;

    public FluidTypeCompletionProvider(boolean cutLastSegment) {
        this.cutLastSegment = cutLastSegment;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getOriginalPosition();
        if (psiElement == null) {
            return;
        }

        Collection<String> possibleTypes = FluidTypeResolver.formatPsiTypeName(psiElement, cutLastSegment);
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
}
