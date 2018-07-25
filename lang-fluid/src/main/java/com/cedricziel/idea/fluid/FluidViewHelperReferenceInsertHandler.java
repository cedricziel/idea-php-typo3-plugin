package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.codeInsight.template.LiveTemplateFactory;
import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.lang.psi.*;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class FluidViewHelperReferenceInsertHandler implements InsertHandler<LookupElement> {
    public static final InsertHandler<LookupElement> INSTANCE = new FluidViewHelperReferenceInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        Editor editor = context.getEditor();
        if (context.getCompletionChar() == '(') {
            context.setAddCompletionChar(false);
        }

        ViewHelper viewHelper = null;
        PsiElement completionParent = item.getPsiElement().getParent();
        if (completionParent instanceof FluidViewHelperReference) {
            viewHelper = findViewHelperByReference((FluidViewHelperReference) completionParent, item);
        } else if(completionParent instanceof FluidBoundNamespace) {
            // find viewhelper
        } else if (completionParent instanceof FluidFieldChain) {
            viewHelper = findViewHelperByFieldPosition((FluidFieldChain) completionParent, item);
        }

        if (viewHelper == null) {
            editor.getDocument().insertString(context.getTailOffset(), "()");

            return;
        }

        if (viewHelper.arguments.size() == 0) {
            editor.getDocument().insertString(context.getTailOffset(), "()");

            return;
        }

        Template t = LiveTemplateFactory.createInlineArgumentListTemplate(viewHelper);
        List<ViewHelperArgument> requiredArguments = viewHelper.getRequiredArguments();
        requiredArguments.forEach(a -> {
            int pos = requiredArguments.indexOf(a);
            t.addVariable("VAR" + pos, new TextExpression(""), true);
        });

        TemplateManager.getInstance(context.getProject()).startTemplate(context.getEditor(), t);
    }

    private ViewHelper findViewHelperByFieldPosition(@NotNull FluidFieldChain completionParent, @NotNull LookupElement item) {
        String[] split = StringUtils.split(item.getLookupString(), ":");
        if (split.length != 2) {
            return null;
        }

        return findByAlias(completionParent.getProject(), completionParent, split[0], split[1]);
    }

    private ViewHelper findViewHelperByReference(@NotNull FluidViewHelperReference completionParent, @NotNull LookupElement item) {
        FluidViewHelperExpr viewHelperExpr = (FluidViewHelperExpr) completionParent.getParent();
        String boundNamespace = viewHelperExpr.getBoundNamespace().getText();
        String lookupString = item.getLookupString();

        return findByAlias(completionParent.getProject(), item.getPsiElement(), boundNamespace, lookupString);
    }

    private ViewHelper findByAlias(Project project, PsiElement element, String boundNamespace, String lookupString) {
        for (NamespaceProvider extension : NamespaceProvider.EP_NAME.getExtensions()) {
            for (FluidNamespace fluidNamespace : extension.provideForElement(element)) {
                if (!fluidNamespace.prefix.equals(boundNamespace)) {
                    continue;
                }

                for (ViewHelperProvider aViewHelperProvider: ViewHelperProvider.EP_NAME.getExtensions()){
                    Map<String, ViewHelper> stringViewHelperMap = aViewHelperProvider.provideForNamespace(project, fluidNamespace.namespace);
                    if (stringViewHelperMap.containsKey(lookupString)) {
                        return stringViewHelperMap.get(lookupString);
                    }
                }
            }
        }

        return null;
    }
}
