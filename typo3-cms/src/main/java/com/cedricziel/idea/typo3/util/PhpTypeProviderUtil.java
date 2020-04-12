package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpTypeProviderUtil {
    /**
     * Deprecated for external plugins
     */
    @Deprecated
    public static String getReferenceSignature(MethodReference methodReference, char trimKey) {
        return getReferenceSignatureByFirstParameter(methodReference, trimKey);
    }

    /**
     * Deprecated for external plugins
     */
    @Deprecated
    public static String getReferenceSignature(MethodReference methodReference, char trimKey, int equalParameterCount) {
        return getReferenceSignatureByFirstParameter(methodReference, trimKey);
    }

    /**
     * Creates a signature for PhpType implementation which must be resolved inside 'getBySignature'
     *
     * eg. foo(MyClass::class) => "#F\\foo|#K#C\\Foo.class"
     *
     * foo($this->foo), foo('foobar')
     */
    @Nullable
    public static String getReferenceSignatureByFirstParameter(@NotNull FunctionReference functionReference, char trimKey) {
        String refSignature = functionReference.getSignature();
        if(StringUtil.isEmpty(refSignature)) {
            return null;
        }

        PsiElement[] parameters = functionReference.getParameters();
        if(parameters.length == 0) {
            return null;
        }

        PsiElement parameter = parameters[0];

        // we already have a string value
        if ((parameter instanceof StringLiteralExpression)) {
            String param = ((StringLiteralExpression)parameter).getContents();
            if (StringUtil.isNotEmpty(param)) {
                return refSignature + trimKey + param;
            }

            return null;
        }

        // whitelist here; we can also provide some more but think of performance
        // Service::NAME, $this->name and Entity::CLASS;
        if (parameter instanceof PhpReference && (parameter instanceof ClassConstantReference || parameter instanceof FieldReference)) {
            String signature = ((PhpReference) parameter).getSignature();
            if (StringUtil.isNotEmpty(signature)) {
                return refSignature + trimKey + signature;
            }
        }

        return null;
    }
}
