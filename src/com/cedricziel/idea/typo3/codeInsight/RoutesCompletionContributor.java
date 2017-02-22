package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.container.RouteProvider;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;

public class RoutesCompletionContributor extends CompletionContributor {
    public RoutesCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement element = parameters.getPosition().getParent();
                        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
                        if (parameterList == null) {
                            return;
                        }

                        PsiElement methodReference = PsiTreeUtil.getParentOfType(element, MethodReference.class);
                        if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getAjaxUrl")) {
                            completeAjaxRoutes(element, resultSet);
                        }
                        if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "buildUriFromRoute")) {
                            completeRoutes(element, resultSet);
                        }
                    }
                }
        );
    }

    private void completeAjaxRoutes(PsiElement element, CompletionResultSet resultSet) {
        RouteProvider routeProvider = getRouteProvider(element);
        routeProvider.ajax().forEach(route -> {
            LookupElementBuilder lookupElement = LookupElementBuilder
                    .create(route.getName())
                    .appendTailText(route.getPath(), true)
                    .withIcon(TYPO3CMSIcons.ROUTE_ICON);

            resultSet.addElement(lookupElement);
        });
    }

    private void completeRoutes(PsiElement element, CompletionResultSet resultSet) {
        RouteProvider routeProvider = getRouteProvider(element);
        routeProvider.all().forEach(route -> {
            LookupElementBuilder lookupElement = LookupElementBuilder
                    .create(route.getName())
                    .appendTailText(route.getPath(), true)
                    .withIcon(TYPO3CMSIcons.ROUTE_ICON);

            resultSet.addElement(lookupElement);
        });
    }

    @NotNull
    private RouteProvider getRouteProvider(PsiElement element) {
        RouteProvider routeProvider = new RouteProvider();
        routeProvider.collect(element.getProject());
        return routeProvider;
    }
}
