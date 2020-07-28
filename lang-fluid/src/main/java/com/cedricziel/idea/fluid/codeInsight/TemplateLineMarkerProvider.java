package com.cedricziel.idea.fluid.codeInsight;

import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.util.FluidUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TemplateLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!PlatformPatterns.psiElement(FluidFile.class).accepts(element)) {
            return;
        }

        Collection<Method> possibleMethodTargetsForControllerAction = FluidUtil.findPossibleMethodTargetsForControllerAction(
            element.getProject(),
            FluidUtil.inferControllerNameFromTemplateFile((FluidFile) element),
            FluidUtil.inferActionNameFromTemplateFile((FluidFile) element)
        );

        if (possibleMethodTargetsForControllerAction.size() > 0) {
            result.add(
                NavigationGutterIconBuilder
                    .create(PhpIcons.METHOD)
                    .setTargets(possibleMethodTargetsForControllerAction)
                    .setTooltipText("Navigate to controller action")
                    .createLineMarkerInfo(element)
            );
        }
    }
}
