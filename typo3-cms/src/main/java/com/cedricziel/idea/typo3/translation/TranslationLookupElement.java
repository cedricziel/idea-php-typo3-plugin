package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.jetbrains.annotations.NotNull;

public class TranslationLookupElement extends LookupElement {

    private StubTranslation translation;

    public TranslationLookupElement(StubTranslation translation) {
        this.translation = translation;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return translation.getId();
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(translation.getIndex() + "  ");
        presentation.setTailText(getLookupString(), true);
        presentation.setIcon(TYPO3CMSIcons.TYPO3_ICON);
        presentation.setTypeText("EXT:" + translation.getExtension());
        presentation.setTypeGrayed(true);
    }
}
