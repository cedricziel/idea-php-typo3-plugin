package com.cedricziel.idea.fluid.variables;

import com.cedricziel.idea.fluid.util.FluidTypeInsertHandler;
import com.cedricziel.idea.fluid.util.FluidTypeResolver;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

public class PhpFluidMethodLookupElement extends PhpLookupElement {
    public PhpFluidMethodLookupElement(@NotNull PhpNamedElement namedElement) {
        super(namedElement);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        FluidTypeInsertHandler.getInstance().handleInsert(context, this);
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        super.renderElement(presentation);

        PhpNamedElement phpNamedElement = this.getNamedElement();

        // reset method to show full name again, which was stripped inside getLookupString
        if (phpNamedElement instanceof Method && FluidTypeResolver.isPropertyShortcutMethod((Method) phpNamedElement)) {
            presentation.setItemText(phpNamedElement.getName());
        }

    }

    @NotNull
    public String getLookupString() {
        String lookupString = super.getLookupString();

        // remove property shortcuts eg getter / issers
        if (this.getNamedElement() instanceof Method && FluidTypeResolver.isPropertyShortcutMethod((Method) this.getNamedElement())) {
            lookupString = FluidTypeResolver.getPropertyShortcutMethodName((Method) this.getNamedElement());
        }

        return lookupString;
    }
}
