package com.cedricziel.idea.typo3.javaScript.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledJsInspection;
import com.cedricziel.idea.typo3.util.JavaScriptUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.frameworks.modules.JSResolvableModuleReference;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MissingModuleJSInspection extends PluginEnabledJsInspection {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDisplayName() {
        return "Missing JavaScript module";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, @NotNull LocalInspectionToolSession localInspectionToolSession) {
        return new JSElementVisitor() {
            @Override
            public void visitJSLiteralExpression(JSLiteralExpression node) {
                PsiReference[] references = node.getReferences();
                for (PsiReference reference : references) {
                    if (!(reference instanceof JSResolvableModuleReference)) {
                        continue;
                    }

                    JSResolvableModuleReference moduleReference = (JSResolvableModuleReference) reference;

                    String canonicalText = moduleReference.getCanonicalText();
                    if (!canonicalText.startsWith(JavaScriptUtil.MODULE_PREFIX)) {
                        super.visitJSLiteralExpression(node);

                        return;
                    }

                    if (JavaScriptUtil.getModuleMap(node.getProject()).containsKey(canonicalText)) {
                        super.visitJSLiteralExpression(node);

                        return;
                    }

                    problemsHolder.registerProblem(node, String.format("Unknown JavaScript module \"%s\"", canonicalText));

                    return;
                }

                super.visitJSLiteralExpression(node);
            }
        };
    }
}
