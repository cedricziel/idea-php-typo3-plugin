package com.cedricziel.idea.typo3.resources;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

public class ResourceLookupElement extends LookupElement {

    private final String resourceIdentifier;

    public ResourceLookupElement(String identifier) {
        this.resourceIdentifier = identifier;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return resourceIdentifier;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(resourceIdentifier);
        presentation.setIcon(AllIcons.FileTypes.Any_type);
    }
}
