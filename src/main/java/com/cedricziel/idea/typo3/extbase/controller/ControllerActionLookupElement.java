package com.cedricziel.idea.typo3.extbase.controller;

import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerActionLookupElement extends LookupElement {

    private final ControllerActionInterface controllerAction;

    public ControllerActionLookupElement(ControllerActionInterface controllerAction) {
        this.controllerAction = controllerAction;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return controllerAction.getName().replace("Action", "");
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(getLookupString());
        presentation.setIcon(AllIcons.Nodes.Method);
        presentation.setTypeText(controllerAction.getControllerName());
    }

    @Override
    public void handleInsert(InsertionContext context) {
        PsiElement elementAt = context.getFile().findElementAt(context.getStartOffset());
        if (elementAt != null) {
            MethodReference methodReference = PhpLangUtil.getMethodReference(elementAt);
            if (methodReference != null) {
                ParameterList parameters = methodReference.getParameterList();
                PsiElement[] children = parameters.getChildren();

                while (parameters.getFirstChild() != null) {
                    parameters.getFirstChild().delete();
                }

                String shortActionName = controllerAction.getName().replace("Action", "");
                parameters.add(PhpPsiElementFactory.createPhpPsiFromText(context.getProject(), StringLiteralExpression.class, "'" + shortActionName + "'"));

                parameters.add(PhpPsiElementFactory.createComma(context.getProject()));

                String shortControllerName = controllerAction.getControllerName().replace("Controller", "");
                parameters.add(PhpPsiElementFactory.createPhpPsiFromText(context.getProject(), StringLiteralExpression.class, "'" + shortControllerName + "'"));

                parameters.add(PhpPsiElementFactory.createComma(context.getProject()));

                String extensionName = controllerAction.getExtensionName();
                parameters.add(PhpPsiElementFactory.createPhpPsiFromText(context.getProject(), StringLiteralExpression.class, "'" + extensionName + "'"));
            }
        }

        super.handleInsert(context);
    }
}
