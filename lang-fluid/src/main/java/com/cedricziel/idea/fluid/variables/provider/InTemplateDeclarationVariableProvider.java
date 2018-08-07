package com.cedricziel.idea.fluid.variables.provider;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.cedricziel.idea.fluid.util.FluidTypeResolver;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InTemplateDeclarationVariableProvider implements VariableProvider {
    private static boolean containsLanguage(@NotNull Language language, @NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile == null) {
            return false;
        }

        FileViewProvider viewProvider = containingFile.getViewProvider();

        return viewProvider.getLanguages().contains(language);
    }

    private static PsiElement extractLanguagePsiElementForElementAtPosition(@NotNull Language language, @NotNull PsiElement psiElement, int offset) {
        FileViewProvider viewProvider = psiElement.getContainingFile().getViewProvider();

        PsiFile psi = viewProvider.getPsi(language);

        PsiElement elementAt = psi.findElementAt(offset);
        if (elementAt == null) {
            return null;
        }

        return elementAt.getParent();
    }

    @NotNull
    private static Collection<String> collectForArrayScopeVariablesFoo(@NotNull Project project, @NotNull Collection<String> typeName, @NotNull FluidVariable psiVariable) {
        Collection<String> previousElements = psiVariable.getTypes();

        String[] strings = typeName.toArray(new String[typeName.size()]);

        for (int i = 1; i <= strings.length - 1; i++) {
            previousElements = FluidTypeResolver.resolveFluidMethodName(project, previousElements, strings[i]);

            // we can stop on empty list
            if (previousElements.size() == 0) {
                return Collections.emptyList();
            }
        }

        return previousElements;
    }

    private static void collectForArrayScopeVariables(PsiElement psiElement, Map<String, FluidVariable> globalVars) {
        if (!containsLanguage(HTMLLanguage.INSTANCE, psiElement)) {
            return;
        }

        PsiFile psi = extractLanguagePsiForElement(HTMLLanguage.INSTANCE, psiElement);
        if (psi == null) {
            return;
        }

        PsiElement elementAt = psi.findElementAt(psiElement.getTextOffset() - 2);
        PsiElement fForTag = PsiTreeUtil.findFirstParent(elementAt, psiElement2 -> PlatformPatterns
            .psiElement(XmlTag.class)
            .withName(PlatformPatterns.string().oneOf("f:for"))
            .accepts(psiElement2)
        );

        if (!(fForTag instanceof XmlTag)) {
            return;
        }

        XmlTag fForElement = (XmlTag) fForTag;
        XmlAttribute eachAttribute = fForElement.getAttribute("each");
        if (eachAttribute == null) {
            return;
        }

        if (eachAttribute.getValueElement() == null) {
            return;
        }

        String variableName = StringUtils.trim(StringUtils.stripStart(StringUtils.stripEnd(eachAttribute.getValueElement().getText(), "}\""), "\"{"));
        if (variableName.split("\\.").length > 0) {
            variableName = variableName.split("\\.")[0];
        }
        if (!globalVars.containsKey(variableName)) {
            return;
        }

        XmlAttribute asAttribute = fForElement.getAttribute("as");
        if (asAttribute == null) {
            return;
        }

        PhpType phpType = new PhpType();

        Collection<String> forTagInIdentifierString = FluidTypeResolver.getForTagIdentifierAsString(fForElement);
        if (forTagInIdentifierString.size() > 1) {

            // nested resolve
            String rootElement = forTagInIdentifierString.iterator().next();
            if (globalVars.containsKey(rootElement)) {
                FluidVariable psiVariable = globalVars.get(rootElement);
                for (String arrayType : collectForArrayScopeVariablesFoo(psiElement.getProject(), forTagInIdentifierString, psiVariable)) {
                    phpType.add(arrayType);
                }
            }

        } else {
            // add single "for" var
            for (String s : globalVars.get(variableName).getTypes()) {
                phpType.add(s);
            }
        }

        String scopeVariable = asAttribute.getValue();

        // find array types; since they are phptypes they ends with []
        Set<String> types = new HashSet<>();
        for (String arrayType : PhpIndex.getInstance(psiElement.getProject()).completeType(psiElement.getProject(), phpType, new HashSet<>()).getTypes()) {
            if (arrayType.endsWith("[]")) {
                types.add(arrayType.substring(0, arrayType.length() - 2));
            }
        }

        // we already have same variable in scope, so merge types
        if (globalVars.containsKey(scopeVariable)) {
            globalVars.get(scopeVariable).getTypes().addAll(types);
        } else {
            globalVars.put(scopeVariable, new FluidVariable(types, asAttribute.getValueElement()));
        }

    }

    private static PsiFile extractLanguagePsiForElement(@NotNull Language language, @NotNull PsiElement psiElement) {
        FileViewProvider viewProvider = psiElement.getContainingFile().getViewProvider();

        int textOffset = psiElement.getTextOffset();
        PsiFile psi = viewProvider.getPsi(language);
        PsiElement elementAt = psi.findElementAt(textOffset);
        if (elementAt == null) {
            return null;
        }

        return psi;
    }

    @Override
    public void provide(@NotNull CompletionParameters parameters, ProcessingContext context, Map<String, FluidVariable> variableMap) {
        PsiElement psiElement = parameters.getPosition();

        getVariablesFromAroundElement(psiElement, variableMap);
    }

    private void getVariablesFromAroundElement(PsiElement psiElement, Map<String, FluidVariable> variableMap) {
        variableMap.putAll(collectXmlViewHelperSetVariables(psiElement));
        variableMap.putAll(collectInlineViewHelperSetVariables(psiElement));

        variableMap.putAll(collectXmlMapViewHelperSetVariables(psiElement));

        collectForArrayScopeVariables(psiElement, variableMap);
    }

    @Override
    public void provide(@NotNull PsiElement element, Map<String, FluidVariable> variableMap) {
        getVariablesFromAroundElement(element, variableMap);
    }

    private Map<String, FluidVariable> collectInlineViewHelperSetVariables(@NotNull PsiElement psiElement) {
        if (!containsLanguage(FluidLanguage.INSTANCE, psiElement)) {
            return new THashMap<>();
        }

        PsiFile psi = extractLanguagePsiForElement(FluidLanguage.INSTANCE, psiElement);
        if (psi == null) {
            return new THashMap<>();
        }

        InlineFVariableVisitor visitor = new InlineFVariableVisitor();
        psi.accept(visitor);

        return visitor.variables;
    }

    private Map<String, FluidVariable> collectXmlViewHelperSetVariables(@NotNull PsiElement psiElement) {
        if (!containsLanguage(HTMLLanguage.INSTANCE, psiElement)) {
            return new THashMap<>();
        }

        PsiFile psi = extractLanguagePsiForElement(HTMLLanguage.INSTANCE, psiElement);
        if (psi == null) {
            return new THashMap<>();
        }

        XmlVariableVisitor visitor = new XmlVariableVisitor();
        psi.accept(visitor);

        return visitor.variables;
    }

    private Map<String, FluidVariable> collectXmlMapViewHelperSetVariables(PsiElement psiElement) {
        if (!containsLanguage(HTMLLanguage.INSTANCE, psiElement)) {
            return new THashMap<>();
        }

        PsiFile psi = extractLanguagePsiForElement(HTMLLanguage.INSTANCE, psiElement);
        if (psi == null) {
            return new THashMap<>();
        }

        XmlFAliasVisitor visitor = new XmlFAliasVisitor();
        psi.accept(visitor);

        return visitor.variables;
    }

    private static class XmlVariableVisitor extends XmlRecursiveElementWalkingVisitor {
        public Map<String, FluidVariable> variables = new THashMap<>();

        @Override
        public void visitXmlTag(XmlTag tag) {
            if (tag.getName().equals("f:variable")) {
                String variableName = tag.getAttributeValue("name");
                if (variableName != null && !variableName.isEmpty()) {
                    variables.put(variableName, new FluidVariable(variableName));
                }
            }

            super.visitXmlTag(tag);
        }
    }

    private static class XmlFAliasVisitor extends XmlRecursiveElementWalkingVisitor {
        public Map<String, FluidVariable> variables = new THashMap<>();

        @Override
        public void visitXmlTag(XmlTag tag) {
            if (tag.getName().equals("f:alias")) {
                XmlAttribute map = tag.getAttribute("map");
                if (map != null) {
                    XmlAttributeValue valueElement = map.getValueElement();
                    if (valueElement != null) {
                        TextRange valueTextRange = valueElement.getValueTextRange();

                        PsiElement fluidElement = extractLanguagePsiElementForElementAtPosition(FluidLanguage.INSTANCE, tag, valueTextRange.getStartOffset() + 1);

                        FluidArrayCreationExpr fluidArray = (FluidArrayCreationExpr) PsiTreeUtil.findFirstParent(fluidElement, x -> x instanceof FluidArrayCreationExpr);
                        if (fluidArray != null) {
                            fluidArray.getArrayKeyList().forEach(fluidArrayKey -> {
                                if (fluidArrayKey.getFirstChild() instanceof FluidStringLiteral) {
                                    String key = ((FluidStringLiteral) fluidArrayKey.getFirstChild()).getContents();
                                    variables.put(key, new FluidVariable(key));

                                    return;
                                }

                                variables.put(fluidArrayKey.getText(), new FluidVariable(fluidArrayKey.getText()));
                            });
                        }
                    }
                }
            }

            super.visitXmlTag(tag);
        }
    }

    private static class InlineFVariableVisitor extends FluidRecursiveWalkingVisitor {
        public Map<String, FluidVariable> variables = new THashMap<>();

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
                            String contents = ((FluidStringLiteral) literal).getContents();
                            variables.put(contents, new FluidVariable(contents));
                        } else {
                            variables.put(literal.getText(), new FluidVariable(literal.getText()));
                        }
                    }
                }
            }

            super.visitViewHelperExpr(o);
        }
    }
}
