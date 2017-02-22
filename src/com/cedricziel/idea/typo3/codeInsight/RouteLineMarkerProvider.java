package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.container.RouteProvider;
import com.cedricziel.idea.typo3.domain.TYPO3RouteDefinition;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RouteLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {

        if (!(element instanceof StringLiteralExpression)) {
            return;
        }

        StringLiteralExpression literalExpression = (StringLiteralExpression) element;
        String value = literalExpression.getContents();

        if (value.isEmpty()) {
            return;
        }

        PsiElement methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
        if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getAjaxUrl") || PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "buildUriFromRoute")) {

            Project project = element.getProject();

            RouteProvider routeProvider = new RouteProvider();
            routeProvider.collect(project);

            if (routeProvider.has(value)) {
                List<TYPO3RouteDefinition> definitions = routeProvider.resolve(value);
                definitions.forEach(def -> {
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(TYPO3CMSIcons.ROUTE_ICON)
                            .setTarget(def.getElement())
                            .setTooltipText("Navigate to route definition");

                    result.add(builder.createLineMarkerInfo(element));
                });

            }
        }
    }
}
