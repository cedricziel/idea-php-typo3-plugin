package com.cedricziel.idea.typo3.icons;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IconLookupElement extends LookupElement {

    private final IconInterface icon;

    public IconLookupElement(@NotNull IconInterface icon) {
        this.icon = icon;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return icon.getName();
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(icon.getName());
        presentation.setIcon(iconFromIconInterface(icon));
        presentation.setTypeText(icon.getExtensionKey());
        presentation.setTypeGrayed(true);
    }

    private Icon iconFromIconInterface(@NotNull IconInterface icon) {
        return AllIcons.FileTypes.Custom;
    }
}
