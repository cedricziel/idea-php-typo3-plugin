package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.index.RouteIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.routing.RouteHelper;
import com.cedricziel.idea.typo3.routing.RouteStub;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RouteLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {

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

            if (RouteIndex.hasRoute(element.getProject(), value)) {
                Collection<RouteStub> routes = RouteIndex.getRoute(element.getProject(), value);
                routes.forEach(def -> {
                    PsiElement[] routeDefinitionElements = RouteHelper.getRouteDefinitionElements(element.getProject(), value);
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(TYPO3CMSIcons.ROUTE_ICON)
                            .setTargets(routeDefinitionElements);

                    if (def.getPath() != null) {
                        builder.setTooltipTitle("Path: " + def.getPath());
                    }

                    result.add(builder.createLineMarkerInfo(element));
                });

            }
        }
    }
}
