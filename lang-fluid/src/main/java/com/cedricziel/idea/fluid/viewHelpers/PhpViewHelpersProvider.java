package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PhpViewHelpersProvider implements ViewHelperProvider {
    private final Key<CachedValue> VIEWHELPER_DEFINITION_KEY = new Key<>("FLUID_VIEWHELPER_DEF");

    @NotNull
    @Override
    public Map<String, ViewHelper> provideForNamespace(@NotNull Project project, @NotNull String namespace) {
        return doProvide(project, namespace);
    }

    @NotNull
    private synchronized Map<String, ViewHelper> doProvide(@NotNull Project project, @NotNull String namespace) {
        HashMap<String, ViewHelper> collectedViewHelpers = new HashMap<>();
        String fqnPart = namespace.replace("/", "\\");
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        Collection<PhpClass> viewHelperClasses = phpIndex.getAllSubclasses("TYPO3Fluid\\Fluid\\Core\\ViewHelper\\ViewHelperInterface");
        for (PhpClass viewHelperPhpClass : viewHelperClasses) {

            String fqn = viewHelperPhpClass.getPresentableFQN();
            if (fqn.startsWith(fqnPart) && !viewHelperPhpClass.isAbstract()) {
                CachedValue cachedViewHelper = viewHelperPhpClass.getUserData(VIEWHELPER_DEFINITION_KEY);
                if (cachedViewHelper != null) {
                    ViewHelper value = (ViewHelper) cachedViewHelper.getValue();
                    collectedViewHelpers.put(value.name, value);

                    continue;
                }

                try {
                    CachedValue<ViewHelper> cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
                        ViewHelperVisitor visitor = new ViewHelperVisitor();
                        viewHelperPhpClass.accept(visitor);

                        ViewHelper viewHelper = new ViewHelper(convertFqnToViewHelperName(fqn.substring(fqnPart.length() + 1)));
                        viewHelper.setFqn(fqn);

                        viewHelper.arguments.putAll(visitor.arguments);

                        return CachedValueProvider.Result.createSingleDependency(viewHelper, viewHelperPhpClass);
                    }, false);

                    viewHelperPhpClass.putUserData(VIEWHELPER_DEFINITION_KEY, cachedValue);

                    collectedViewHelpers.put(cachedValue.getValue().name, cachedValue.getValue());
                } catch (IllegalArgumentException e) {
                    e.getMessage();
                }
            }
        }

        return collectedViewHelpers;
    }

    private String convertFqnToViewHelperName(@NotNull String namePart) {
        namePart = namePart.substring(0, namePart.length() - "ViewHelper".length());

        String[] parts = StringUtils.split(namePart, "\\");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 1) {
                parts[i] = parts[i].substring(0, 1).toLowerCase() + parts[i].substring(1);
            } else {
                parts[i] = parts[i].substring(0, 1).toLowerCase();
            }
        }

        return StringUtils.join(parts, ".");
    }

    private static class ViewHelperVisitor extends PhpRecursiveElementVisitor {
        Map<String, ViewHelperArgument> arguments = new HashMap<>();

        @Override
        public void visitPhpMethodReference(MethodReference reference) {
            if (reference.getName() == null || !reference.getName().equals("registerArgument") || reference.getParameterList() == null) {
                super.visitPhpMethodReference(reference);

                return;
            }

            ViewHelperArgument argument = null;
            ParameterList parameterList = reference.getParameterList();
            PsiElement[] parameters = parameterList.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                PsiElement p = parameters[i];
                if (p == null) {
                    continue;
                }

                // name
                if (i == 0) {
                    if (!(p instanceof StringLiteralExpression)) {
                        throwWithMessage("Cant process variable arguments for now");
                    }

                    if (p instanceof StringLiteralExpression) {
                        argument = new ViewHelperArgument(((StringLiteralExpression) p).getContents());
                    }
                }

                if (argument == null) {
                    continue;
                }

                if (i == 1) {
                    if (p instanceof StringLiteralExpression) {
                        argument.setType(((StringLiteralExpression) p).getContents());
                    } else if (p instanceof ClassConstantReference) {
                        String fqn = ((ClassConstantReference) p).getFQN();
                        if (fqn != null) {
                            argument.setType(fqn);
                        }
                    } else {
                        throwWithMessage("Can only compute strings and class constants for now");
                    }
                }

                if (i == 2) {
                    if (p instanceof StringLiteralExpression) {
                        argument.setDocumentation(((StringLiteralExpression) p).getContents());
                    } else {
                        throwWithMessage("Can only process strings as argument documentation");
                    }
                }

                if (i == 3) {
                    if (p instanceof Constant) {
                        argument.setRequired(StringUtils.equalsIgnoreCase(p.getText(), "true"));
                    } else if (p instanceof ConstantReference) {
                        argument.setRequired(StringUtils.equalsIgnoreCase(p.getText(), "true"));
                    } else {
                        throwWithMessage("Can only process booleans for now as required arguments");
                    }
                }
            }

            if (argument != null) {
                arguments.put(argument.name, argument);
            }

            super.visitPhpMethodReference(reference);
        }

        private void throwWithMessage(String message) {
            if (ApplicationManager.getApplication().isUnitTestMode()) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
