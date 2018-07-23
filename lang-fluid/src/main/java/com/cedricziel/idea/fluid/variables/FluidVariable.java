package com.cedricziel.idea.fluid.variables;

import icons.FluidIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FluidVariable {
    public String description;
    public Icon icon;
    public String identifier;

    public FluidVariable(@NotNull String identifier) {
        this(identifier, FluidIcons.VARIABLE);
    }

    public FluidVariable(@NotNull String identifier, @Nullable Icon icon) {
        this(identifier, "", icon);
    }

    public FluidVariable(@NotNull String identifier, @NotNull String description, @Nullable Icon icon) {
        this.identifier = identifier;
        this.description = description;

        this.icon = icon != null ? icon : FluidIcons.VARIABLE;
    }

    public FluidVariable setDescription(String description) {
        this.description = description;

        return this;
    }
}
