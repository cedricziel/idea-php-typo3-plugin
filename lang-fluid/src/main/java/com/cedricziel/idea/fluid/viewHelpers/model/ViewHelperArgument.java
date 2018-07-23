package com.cedricziel.idea.fluid.viewHelpers.model;

import org.jetbrains.annotations.NotNull;

public class ViewHelperArgument {
    public String name;
    public boolean required;
    public String type;
    public String documentation;

    public ViewHelperArgument(@NotNull String name) {
        this.name = name;
        this.required = false;
    }

    public ViewHelperArgument setType(@NotNull String type) {
        this.type = type;

        return this;
    }

    public String getType() {
        return type;
    }

    public void setDocumentation(@NotNull String documentation) {
        this.documentation = documentation;
    }

    public ViewHelperArgument setRequired(boolean required) {
        this.required = required;

        return this;
    }
}
