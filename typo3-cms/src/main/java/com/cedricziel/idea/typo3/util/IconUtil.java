package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.icons.IconLookupElement;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class IconUtil {

    public static final String ICON_REGISTRY_CLASS = "TYPO3\\CMS\\Core\\Imaging\\IconRegistry";

    public static IconLookupElement[] createIconLookupElements(@NotNull Project project) {
        return Arrays.stream(IconIndex.getAllIcons(project))
            .map(IconLookupElement::new).toArray(IconLookupElement[]::new);
    }
}
