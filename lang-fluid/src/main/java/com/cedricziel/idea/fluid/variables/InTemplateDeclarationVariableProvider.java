package com.cedricziel.idea.fluid.variables;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.XmlRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InTemplateDeclarationVariableProvider implements VariableProvider {
    @Override
    @NotNull
    public Collection<FluidVariable> provide(@NotNull CompletionParameters parameters, ProcessingContext context) {
        PsiElement psiElement = parameters.getPosition();

        return getVariablesFromAroundElement(psiElement);
    }

    @NotNull
    private List<FluidVariable> getVariablesFromAroundElement(PsiElement psiElement) {
        List<FluidVariable> result = new ArrayList<>();
        result.addAll(collectXmlViewHelperSetVariables(psiElement));
        result.addAll(collectInlineViewHelperSetVariables(psiElement));
        return result;
    }

    @NotNull
    @Override
    public Collection<FluidVariable> provide(@NotNull PsiElement element) {
        return getVariablesFromAroundElement(element);
    }

    private Collection<FluidVariable> collectInlineViewHelperSetVariables(@NotNull PsiElement psiElement) {
        FileViewProvider viewProvider = psiElement.getContainingFile().getViewProvider();
        if (!viewProvider.getLanguages().contains(FluidLanguage.INSTANCE)) {
            return ContainerUtil.emptyList();
        }

        int textOffset = psiElement.getTextOffset();
        FluidFile psi = (FluidFile) viewProvider.getPsi(FluidLanguage.INSTANCE);
        PsiElement elementAt = psi.findElementAt(textOffset);
        if (elementAt == null) {
            return ContainerUtil.emptyList();
        }

        InlineFSetVisitor visitor = new InlineFSetVisitor();
        psi.accept(visitor);

        return visitor.variables;
    }

    private Collection<FluidVariable> collectXmlViewHelperSetVariables(@NotNull PsiElement psiElement) {
        FileViewProvider viewProvider = psiElement.getContainingFile().getViewProvider();
        if (!viewProvider.getLanguages().contains(HTMLLanguage.INSTANCE)) {
            return ContainerUtil.emptyList();
        }

        int textOffset = psiElement.getTextOffset();
        HtmlFileImpl psi = (HtmlFileImpl) viewProvider.getPsi(HTMLLanguage.INSTANCE);
        PsiElement elementAt = psi.findElementAt(textOffset);
        if (elementAt == null) {
            return ContainerUtil.emptyList();
        }

        XmlFSetVisitor visitor = new XmlFSetVisitor();
        psi.accept(visitor);

        return visitor.variables;
    }

    private static class XmlFSetVisitor extends XmlRecursiveElementWalkingVisitor {
        public List<FluidVariable> variables = new ArrayList<>();

        @Override
        public void visitXmlTag(XmlTag tag) {
            if (tag.getName().equals("f:variable")) {
                String variableName = tag.getAttributeValue("name");
                if (variableName != null && !variableName.isEmpty()) {
                    variables.add(new FluidVariable(variableName));
                }
            }

            super.visitXmlTag(tag);
        }
    }

    private static class InlineFSetVisitor extends FluidRecursiveWalkingVisitor {
        public List<FluidVariable> variables = new ArrayList<>();

        @Override
        public void visitViewHelperExpr(@NotNull FluidViewHelperExpr o) {
            String presentableName = o.getPresentableName();
            if (presentableName.equals("f:variable")) {
                FluidViewHelperArgumentList viewHelperArgumentList = o.getViewHelperArgumentList();
                if (viewHelperArgumentList != null) {
                    FluidViewHelperArgument argument = viewHelperArgumentList.getArgument("name");
                    if (argument != null && argument.getArgumentValue() != null && argument.getArgumentValue().getLiteral() != null) {
                        FluidLiteral literal = argument.getArgumentValue().getLiteral();
                        if (literal instanceof FluidStringLiteral) {
                            variables.add(new FluidVariable(((FluidStringLiteral) literal).getContents()));
                        } else {
                            variables.add(new FluidVariable(literal.getText()));
                        }
                    }
                }
            }

            super.visitViewHelperExpr(o);
        }
    }
}
