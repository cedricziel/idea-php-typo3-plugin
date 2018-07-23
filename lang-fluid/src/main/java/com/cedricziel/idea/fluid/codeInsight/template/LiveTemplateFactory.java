package com.cedricziel.idea.fluid.codeInsight.template;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.TemplateImpl;
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

            if (requiredArguments.size() > pos + 1){
                sb.append(", ");
            }
        });
        sb.append(")");

        TemplateImpl template = new TemplateImpl("params", sb.toString(), "Fluid");
        template.setDescription("smart viewhelper parameters completion");
        template.setToReformat(true);

        return template;
    }
}
