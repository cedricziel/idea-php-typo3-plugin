package com.cedricziel.idea.fluid.codeInsight;

import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.Method;
import icons.FluidIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ControllerLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!PlatformPatterns.psiElement(PhpTokenTypes.IDENTIFIER).withParent(PlatformPatterns.psiElement(Method.class).withName(PlatformPatterns.string().endsWith("Action"))).accepts(element)) {
            return;
        }

        Collection<FluidFile> possibleMatchedTemplates = FluidUtil.findTemplatesForControllerAction((Method) element.getParent());
        if (possibleMatchedTemplates.size() > 0) {
            result.add(
                NavigationGutterIconBuilder
                    .create(FluidIcons.TEMPLATE_LINE_MARKER)
                    .setTargets(possibleMatchedTemplates)
                    .setTooltipText("Navigate to template")
                    .createLineMarkerInfo(element)
            );
        }
    }
}
