package com.cedricziel.idea.fluid.extensionPoints;

import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ViewHelperProvider {
    ExtensionPointName<ViewHelperProvider> EP_NAME = ExtensionPointName.create("com.cedricziel.idea.fluid.provider.viewHelper");

    @NotNull Map<String, ViewHelper> provideForNamespace(@NotNull Project project, @NotNull String namespace);
}
