package com.cedricziel.idea.typo3.routing;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.jetbrains.annotations.NotNull;

public class RouteLookupElement extends LookupElement {
    private final RouteInterface route;

    public RouteLookupElement(@NotNull RouteInterface routeStub) {
        this.route = routeStub;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return route.getName();
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(getLookupString());
        presentation.setIcon(TYPO3CMSIcons.ROUTE_ICON);
        presentation.setTypeText(route.getController());
        presentation.setTypeGrayed(true);
    }
}
