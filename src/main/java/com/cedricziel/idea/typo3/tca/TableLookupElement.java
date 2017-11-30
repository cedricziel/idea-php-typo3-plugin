package com.cedricziel.idea.typo3.tca;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

public class TableLookupElement extends LookupElement {

    @NotNull
    private final String tablename;

    public TableLookupElement(@NotNull String tableName) {
        this.tablename = tableName;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return tablename;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {

        presentation.setItemText(getLookupString());
        presentation.setIcon(AllIcons.Nodes.DataTables);
    }
}
