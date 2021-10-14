package com.cedricziel.idea.fluid.editor;

import com.cedricziel.idea.fluid.index.FluidIndexUtil;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Injects the Fluid Language into relevant places such as string literal attributes on XML elements.
 * <p>
 * TODO: Provide Extension Point to enable providing allowed attributes f.e. from ViewHelper definitions in PHP.
 */
public class FluidInjector implements MultiHostInjector {

    private static final String[] ATTRIBUTES = {
        "each", "as"
    };

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (context.getLanguage() == XMLLanguage.INSTANCE) return;
        final Project project = context.getProject();
        if (!FluidIndexUtil.hasFluid(project)) return;

        final PsiElement parent = context.getParent();
        if (context instanceof XmlAttributeValueImpl && parent instanceof XmlAttribute) {
            final int length = context.getTextLength();
            final String name = ((XmlAttribute) parent).getName();

            if (isInjectableAttribute(project, length, name)) {
                registrar
                    .startInjecting(FluidLanguage.INSTANCE)
                    .addPlace(null, null, (PsiLanguageInjectionHost) context, ElementManipulators.getValueTextRange(context))
                    .doneInjecting();
            }
        }
    }

    protected boolean isInjectableAttribute(@NotNull Project project, int valueLength, String name) {
        return (Arrays.asList(ATTRIBUTES).contains(name)) && valueLength > 0;
    }

    @NotNull
    @Override
    public List<Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlAttributeValue.class);
    }
}
