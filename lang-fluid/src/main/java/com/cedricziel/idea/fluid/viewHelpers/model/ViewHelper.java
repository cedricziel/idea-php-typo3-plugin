package com.cedricziel.idea.fluid.viewHelpers.model;

import gnu.trove.THashMap;
import icons.FluidIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewHelper {
    public final Map<String, ViewHelperArgument> arguments;
    public final String name;
    public String extensionName;
    public String fqn;
    public Icon icon;
    private String documentation;

    public ViewHelper(@NotNull String name) {
        this.name = name;
        this.arguments = new THashMap<>();
        this.icon = FluidIcons.VIEW_HELPER;
    }

    public ViewHelper create(@NotNull String name) {
        return new ViewHelper(name);
    }

    public ViewHelper addArgument(@NotNull String name, @NotNull ViewHelperArgument argument) {
        this.arguments.put(name, argument);

        return this;
    }


    public ViewHelper setDocumentation(@NotNull String documentation) {
        this.documentation = documentation;

        return this;
    }

    public @Nullable String getDocumentation() {
        return documentation;
    }

    @NotNull
    public List<ViewHelperArgument> getRequiredArguments() {
        List<ViewHelperArgument> requiredArguments = new ArrayList<>();
        arguments.forEach((name, def) -> {
            if (def.required) {
                requiredArguments.add(def);
            }
        });

        return requiredArguments;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }
}
