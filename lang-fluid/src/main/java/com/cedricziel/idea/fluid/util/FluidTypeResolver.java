package com.cedricziel.idea.fluid.util;

import com.cedricziel.idea.fluid.extensionPoints.VariableProvider;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldChain;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldChainExpr;
import com.cedricziel.idea.fluid.lang.psi.FluidFieldExpr;
import com.cedricziel.idea.fluid.lang.psi.FluidInlineChain;
import com.cedricziel.idea.fluid.variables.FluidTypeContainer;
import com.cedricziel.idea.fluid.variables.FluidVariable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class FluidTypeResolver {
    private static final String[] PROPERTY_SHORTCUTS = new String[]{"get", "is", "has"};

    public static Collection<String> formatPsiTypeName(@NotNull PsiElement psiElement) {
        return formatPsiTypeName(psiElement, false);
    }

    public static Collection<String> formatPsiTypeName(@NotNull PsiElement psiElement, boolean removeLast) {
        List<String> possibleTypes = new ArrayList<>();
        if (psiElement.getPrevSibling() instanceof FluidInlineChain && psiElement.getPrevSibling().getFirstChild() != null && psiElement.getPrevSibling().getFirstChild() instanceof FluidFieldExpr) {
            possibleTypes.add(((FluidFieldExpr) psiElement.getPrevSibling().getFirstChild()).getName());
        } else if (psiElement.getPrevSibling() instanceof FluidInlineChain && psiElement.getPrevSibling().getFirstChild() instanceof FluidFieldChainExpr) {
            psiElement = PsiTreeUtil.getDeepestLast(psiElement.getPrevSibling().getFirstChild());
        }

        if (psiElement.getParent() instanceof FluidFieldChain) {
            FluidFieldChainExpr fieldExpression = (FluidFieldChainExpr) PsiTreeUtil.findFirstParent(psiElement, e -> e instanceof FluidFieldChainExpr);
            PsiTreeUtil.treeWalkUp(psiElement.getParent(), fieldExpression, (psiElement1, psiElement2) -> {
                if (psiElement1 instanceof FluidFieldChainExpr) {
                    FluidFieldExpr childOfType = PsiTreeUtil.findChildOfType(psiElement1, FluidFieldExpr.class);
                    if (childOfType != null) {
                        possibleTypes.add(childOfType.getName());
                    }
                    return false;
                } else {
                    possibleTypes.add(((FluidFieldChain) psiElement1).getName());
                }

                return true;
            });
        }

        if (psiElement.getParent() instanceof FluidInlineChain) {
            // TODO
        }

        possibleTypes.sort(Collections.reverseOrder());

        if (removeLast && possibleTypes.size() > 0) {
            possibleTypes.remove(possibleTypes.size() - 1);
        }

        return possibleTypes;
    }

    /**
     * Collects all possible variables in given path for last given item of "typeName"
     *
     * @param types Variable path "foo.bar" => ["foo", "bar"]
     * @return types for last item of typeName parameter
     */
    @NotNull
    public static Collection<FluidTypeContainer> resolveFluidMethodName(@NotNull PsiElement psiElement, @NotNull Collection<String> types) {
        if (types.size() == 0) {

            return Collections.emptyList();
        }

        String rootType = types.iterator().next();
        Collection<FluidVariable> rootVariables = getRootVariableByName(psiElement, rootType);
        if (types.size() == 1) {

            return FluidTypeContainer.fromCollection(psiElement.getProject(), rootVariables);
        }

        Collection<FluidTypeContainer> type = FluidTypeContainer.fromCollection(psiElement.getProject(), rootVariables);
        Collection<List<FluidTypeContainer>> previousElements = new ArrayList<>();
        previousElements.add(new ArrayList<>(type));

        String[] typeNames = types.toArray(new String[0]);
        for (int i = 1; i <= typeNames.length - 1; i++) {
            type = resolveFluidMethodName(type, typeNames[i], previousElements);
            previousElements.add(new ArrayList<>(type));

            // we can stop on empty list
            if (type.size() == 0) {

                return Collections.emptyList();
            }
        }

        return type;
    }

    private static Collection<FluidVariable> getRootVariableByName(PsiElement psiElement, String rootType) {
        List<FluidVariable> variables = new ArrayList<>();
        for (Map.Entry<String, FluidVariable> variable : collectScopeVariables(psiElement).entrySet()) {
            if (variable.getKey().equals(rootType)) {
                variables.add(variable.getValue());
            }
        }

        return variables;
    }

    public static boolean isPropertyShortcutMethod(Method method) {

        for (String shortcut : PROPERTY_SHORTCUTS) {
            if (method.getName().startsWith(shortcut) && method.getName().length() > shortcut.length()) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public static String getPropertyShortcutMethodName(@NotNull Method method) {
        String methodName = method.getName();

        for (String shortcut : PROPERTY_SHORTCUTS) {
            // strip possible property shortcut and make it lcfirst
            if (method.getName().startsWith(shortcut) && method.getName().length() > shortcut.length()) {
                methodName = methodName.substring(shortcut.length());
                return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
            }
        }

        return methodName;
    }

    public static Collection<PhpClass> getClassFromPhpTypeSet(Project project, Set<String> types) {

        PhpType phpType = new PhpType();
        for (String type : types) {
            phpType.add(type);
        }

        List<PhpClass> phpClasses = new ArrayList<>();
        for (String typeName : PhpIndex.getInstance(project).completeType(project, phpType, new HashSet<>()).getTypes()) {
            if (typeName.startsWith("\\")) {
                PhpClass phpClass = getClassInterface(project, typeName);
                if (phpClass != null) {
                    phpClasses.add(phpClass);
                }
            }
        }

        return phpClasses;
    }

    public static Collection<PhpClass> getClassFromPhpTypeSetArrayClean(Project project, Set<String> types) {

        PhpType phpType = new PhpType();
        for (String type : types) {
            phpType.add(type);
        }

        ArrayList<PhpClass> phpClasses = new ArrayList<>();

        for (String typeName : PhpIndex.getInstance(project).completeType(project, phpType, new HashSet<>()).getTypes()) {
            if (typeName.startsWith("\\")) {

                // we clean array types \Foo[]
                if (typeName.endsWith("[]")) {
                    typeName = typeName.substring(0, typeName.length() - 2);
                }

                PhpClass phpClass = getClassInterface(project, typeName);
                if (phpClass != null) {
                    phpClasses.add(phpClass);
                }
            }
        }

        return phpClasses;
    }

    @Nullable
    public static PhpClass getClassInterface(Project project, @NotNull String className) {
        Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getAnyByFQN(className);
        return phpClasses.size() == 0 ? null : phpClasses.iterator().next();
    }

    public static Collection<PhpClass> getClassesInterface(Project project, @NotNull String className) {
        return PhpIndex.getInstance(project).getAnyByFQN(className);
    }

    /**
     * "phpNamedElement.variableName", "phpNamedElement.getVariableName" will resolve php type eg method
     *
     * @param phpNamedElement php class method or field
     * @param variableName    variable name shortcut property possible
     * @return matched php types
     */
    public static Collection<? extends PhpNamedElement> getFluidPhpNameTargets(PhpNamedElement phpNamedElement, String variableName) {

        Collection<PhpNamedElement> targets = new ArrayList<>();
        if (phpNamedElement instanceof PhpClass) {

            for (Method method : ((PhpClass) phpNamedElement).getMethods()) {
                String methodName = method.getName();
                if (method.getModifier().isPublic() && (methodName.equalsIgnoreCase(variableName) || isPropertyShortcutMethodEqual(methodName, variableName))) {
                    targets.add(method);
                }
            }

            for (Field field : ((PhpClass) phpNamedElement).getFields()) {
                String fieldName = field.getName();
                if (field.getModifier().isPublic() && fieldName.equalsIgnoreCase(variableName)) {
                    targets.add(field);
                }
            }

        }

        return targets;
    }

    public static boolean isPropertyShortcutMethodEqual(String methodName, String variableName) {

        for (String shortcut : PROPERTY_SHORTCUTS) {
            if (methodName.equalsIgnoreCase(shortcut + variableName)) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, FluidVariable> collectScopeVariables(PsiElement psiElement) {
        return collectScopeVariables(psiElement, new HashSet<>());
    }

    @NotNull
    public static Map<String, FluidVariable> collectScopeVariables(@NotNull PsiElement psiElement, @NotNull Set<VirtualFile> visitedFiles) {
        Map<String, Set<String>> globalVars = new HashMap<>();
        Map<String, FluidVariable> variables = new THashMap<>();

        VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
        if (visitedFiles.contains(virtualFile)) {
            return variables;
        }

        visitedFiles.add(virtualFile);

        for (VariableProvider extension : VariableProvider.EP_NAME.getExtensions()) {
            extension.provide(psiElement, variables);
        }

        for (Map.Entry<String, Set<String>> entry : globalVars.entrySet()) {
            Set<String> types = entry.getValue();

            // collect iterator
            types.addAll(collectIteratorReturns(psiElement, entry.getValue()));

            // convert to variable model
            variables.put(entry.getKey(), new FluidVariable(types, null));
        }

        // check if we are in a loop and resolve types ending with []
        collectForArrayScopeVariables(psiElement, variables);

        return variables;
    }

    private static void collectForArrayScopeVariables(PsiElement psiElement, Map<String, FluidVariable> globalVars) {

    }

    @NotNull
    private static Set<String> collectIteratorReturns(@NotNull PsiElement psiElement, @NotNull Set<String> types) {
        Set<String> arrayValues = new HashSet<>();
        for (String type : types) {
            PhpClass phpClass = getClassInterface(psiElement.getProject(), type);

            if (phpClass == null) {
                continue;
            }

            for (String methodName : new String[]{"getIterator", "__iterator", "current"}) {
                Method method = phpClass.findMethodByName(methodName);
                if (method != null) {
                    // @method Foo __iterator
                    // @method Foo[] __iterator
                    Set<String> iteratorTypes = method.getType().getTypes();
                    if ("__iterator".equals(methodName) || "current".equals(methodName)) {
                        arrayValues.addAll(iteratorTypes.stream().map(x ->
                            !x.endsWith("[]") ? x + "[]" : x
                        ).collect(Collectors.toSet()));
                    } else {
                        // Foobar[]
                        for (String iteratorType : iteratorTypes) {
                            if (iteratorType.endsWith("[]")) {
                                arrayValues.add(iteratorType);
                            }
                        }
                    }
                }
            }
        }

        return arrayValues;
    }

    private static Collection<FluidTypeContainer> resolveFluidMethodName(Collection<FluidTypeContainer> previousElement, String typeName, Collection<List<FluidTypeContainer>> twigTypeContainer) {
        ArrayList<FluidTypeContainer> phpNamedElements = new ArrayList<>();
        for (FluidTypeContainer phpNamedElement : previousElement) {
            if (phpNamedElement.getPhpNamedElement() != null) {
                for (PhpNamedElement target : getFluidPhpNameTargets(phpNamedElement.getPhpNamedElement(), typeName)) {
                    PhpType phpType = target.getType();
                    for (String typeString : phpType.getTypes()) {
                        PhpNamedElement phpNamedElement1 = getClassInterface(phpNamedElement.getPhpNamedElement().getProject(), typeString);
                        if (phpNamedElement1 != null) {
                            phpNamedElements.add(new FluidTypeContainer(phpNamedElement1));
                        }
                    }
                }
            }
        }

        return phpNamedElements;
    }

    public static Set<String> resolveFluidMethodName(Project project, Collection<String> previousElement, String typeName) {

        Set<String> types = new HashSet<>();
        for (String prevClass : previousElement) {
            for (PhpClass phpClass : getClassesInterface(project, prevClass)) {
                for (PhpNamedElement target : getFluidPhpNameTargets(phpClass, typeName)) {
                    types.addAll(target.getType().getTypes());
                }
            }
        }

        return types;
    }

    public static String getTypeDisplayName(Project project, Set<String> types) {

        Collection<PhpClass> classFromPhpTypeSet = getClassFromPhpTypeSet(project, types);
        if (classFromPhpTypeSet.size() > 0) {
            return classFromPhpTypeSet.iterator().next().getPresentableFQN();
        }

        PhpType phpType = new PhpType();
        for (String type : types) {
            phpType.add(type);
        }
        PhpType phpTypeFormatted = PhpIndex.getInstance(project).completeType(project, phpType, new HashSet<>());

        if (phpTypeFormatted.getTypes().size() > 0) {
            return StringUtils.join(phpTypeFormatted.getTypes(), "|");
        }

        if (types.size() > 0) {
            return types.iterator().next();
        }

        return "";
    }

    /**
     * Get the "for IN" variable identifier as separated string
     * <p>
     * {% for car in "cars" %}
     * {% for car in "cars"|length %}
     * {% for car in "cars.test" %}
     */
    @NotNull
    public static Collection<String> getForTagIdentifierAsString(XmlTag forTag) {
        XmlAttribute each = forTag.getAttribute("each");
        if (each == null || each.getValueElement() == null) {
            return ContainerUtil.emptyList();
        }

        PsiElement fluidElement = FluidUtil.retrieveFluidElementAtPosition(each.getValueElement());
        if (fluidElement == null) {
            return Collections.emptyList();
        }

        PsiElement deepestFirst = PsiTreeUtil.getDeepestFirst(fluidElement);

        return FluidTypeResolver.formatPsiTypeName(deepestFirst);
    }
}
