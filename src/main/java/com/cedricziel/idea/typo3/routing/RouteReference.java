package com.cedricziel.idea.typo3.routing;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RouteReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String routeName;

    public RouteReference(@NotNull StringLiteralExpression element) {
        super(element);

        this.routeName = element.getContents();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(RouteHelper.getRouteDefinitionElements(myElement.getProject(), routeName));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Collection<LookupElement> routesLookupElements = RouteHelper.getRoutesLookupElements(myElement.getProject());
        return routesLookupElements.toArray();
    }
}
