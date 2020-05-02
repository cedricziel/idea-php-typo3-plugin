package com.cedricziel.idea.typo3.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PhpLangUtil {
    @Nullable
    public static String getParameterName(@NotNull StringLiteralExpression element) {
        return getParameterName(element, 0);
    }

    @Nullable
    public static String getParameterName(@NotNull StringLiteralExpression element, @NotNull Integer position) {
        Parameter paramAtPosition = getParamAtPosition(element, position);
        if (paramAtPosition == null) {
            return null;
        }

        return paramAtPosition.getName();
    }

    @Nullable
    private static Parameter getParamAtPosition(@NotNull StringLiteralExpression element, @NotNull Integer position) {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return null;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
        if (methodReference == null) {
            return null;
        }

        String methodName = methodReference.getName();
        ClassReference classReference = PsiTreeUtil.getChildOfType(methodReference, ClassReference.class);

        // may be null if the call is chained
        String name = extractFqnFromClassReference(methodReference, classReference);

        if (name == null || methodName == null) {
            return null;
        }

        // there can be multiple classes in one project scope that share the same FQN
        Collection<PhpClass> phpClasses = PhpIndex.getInstance(element.getProject()).getClassesByFQN(name);
        for (PhpClass c : phpClasses) {
            Method method = c.findMethodByName(methodName);

            ParameterList originalMethodParameterList = PsiTreeUtil.getChildOfType(method, ParameterList.class);
            if (originalMethodParameterList != null) {
                List<Parameter> parameters = PsiTreeUtil.getChildrenOfTypeAsList(originalMethodParameterList, Parameter.class);
                if (parameters.size() > 0 && parameters.get(position) != null) {

                    return parameters.get(position);
                }
            }
        }

        return null;
    }

    public static String getMethodName(@NotNull PsiElement element) {
        MethodReference methodReference = getMethodReference(element);
        if (methodReference == null) {
            return null;
        }

        return methodReference.getName();
    }

    public static MethodReference getMethodReference(@NotNull PsiElement element) {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return null;
        }

        return PsiTreeUtil.getParentOfType(element, MethodReference.class);
    }

    public static String getClassName(@NotNull PsiElement element) {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return null;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
        if (methodReference == null) {
            return null;
        }

        Variable variableBeingCalledOn = PsiTreeUtil.findChildOfType(methodReference, Variable.class);
        if (variableBeingCalledOn != null && variableBeingCalledOn.getInferredType() != null) {
            PhpType inferredType = variableBeingCalledOn.getInferredType();
            return inferredType.toString();
        }

        ClassReference classReference = PsiTreeUtil.getChildOfType(methodReference, ClassReference.class);

        return extractFqnFromClassReference(methodReference, classReference);
    }

    @Nullable
    private static String extractFqnFromClassReference(MethodReference methodReference, ClassReference classReference) {
        String fqn;
        if (classReference == null) {
            Method method = (Method) methodReference.resolve();
            if (method != null && method.getContainingClass() != null) {
                fqn = method.getContainingClass().getFQN();
            } else {
                fqn = null;
            }
        } else {
            fqn = classReference.getFQN();
        }

        return fqn;
    }

    public static int getParameterPosition(@NotNull PsiElement element) {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return -1;
        }

        return Arrays.asList(parameterList.getParameters()).indexOf(element);
    }
}
