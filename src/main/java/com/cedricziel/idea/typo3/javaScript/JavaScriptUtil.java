package com.cedricziel.idea.typo3.javaScript;

import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.NotNull;

public class JavaScriptUtil {
    @NotNull
    public static String convertModuleNameToResourceIdentifier(@NotNull String s) {
        String withoutT3Identifier = s.substring("TYPO3/CMS/".length());
        String[] parts = withoutT3Identifier.split("/");
        if (parts.length == 0) {
            return "";
        }

        String extensionName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parts[0]);

        return "EXT:" + extensionName + "/Resources/Public/JavaScripts/" + parts[parts.length - 1] + ".js";
    }
}
