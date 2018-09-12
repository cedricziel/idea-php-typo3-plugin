package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.RouteIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.routing.RouteHelper;
import com.cedricziel.idea.typo3.routing.RouteStub;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RouteLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!TYPO3CMSProjectSettings.isEnabled(element)) {
            return;
        }

        if (!getPositionPattern().accepts(element)) {
            return;
        }

        StringLiteralExpression literalExpression = (StringLiteralExpression) element.getParent();
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

    @NotNull
    private ElementPattern<PsiElement> getPositionPattern() {

        return PlatformPatterns.or(
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE).withParent(StringLiteralExpression.class),
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL).withParent(StringLiteralExpression.class)
        );
    }
}
