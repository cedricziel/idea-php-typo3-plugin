package com.cedricziel.idea.fluid.codeInsight.template;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LiveTemplateFactory {
    public static Template createInlineArgumentListTemplate(@NotNull ViewHelper viewHelper) {
        StringBuilder sb = new StringBuilder();
        List<ViewHelperArgument> requiredArguments = viewHelper.getRequiredArguments();

        sb.append("(");
        requiredArguments.forEach(a -> {
            int pos = requiredArguments.indexOf(a);
            sb.append(a.name).append(": ").append("$VAR").append(pos).append("$");

            if (requiredArguments.size() > pos + 1) {
                sb.append(", ");
            }
        });
        sb.append(")");

        TemplateImpl template = new TemplateImpl("params", sb.toString(), "Fluid");
        template.setDescription("smart viewhelper parameters completion");
        template.setToReformat(true);

        return template;
    }

    public static Template createTagModeForLoopTemplate(@NotNull PsiElement wrap) {

        String sb = "<f:for each=\"$EACH$\" as=\"$AS$\">\n" +
            "  $END$\n" +
            "</f:for>\n";

        TemplateImpl template = new TemplateImpl("f:for", sb, "Fluid");
        template.setDescription("f:for loop over an expression");
        template.setToReformat(true);

        return template;
    }

    public static Template createTagModeAliasTemplate(@NotNull PsiElement wrap) {

        String sb = "<f:alias map=\"{$ALIAS$: $EXPR$}\">\n" +
            "  $END$\n" +
            "</f:alias>\n";

        TemplateImpl template = new TemplateImpl("f:alias", sb, "Fluid");
        template.setDescription("f:alias the current expression");
        template.setToReformat(true);

        return template;
    }

    public static Template createInlinePipeToDebugTemplate(@NotNull PsiElement source) {

        TemplateImpl template = new TemplateImpl("f:debug", "{ $EXPR$ -> f:debug() }", "Fluid");
        template.setDescription("f:debug the expression result");
        template.setToReformat(true);

        return template;
    }
}
