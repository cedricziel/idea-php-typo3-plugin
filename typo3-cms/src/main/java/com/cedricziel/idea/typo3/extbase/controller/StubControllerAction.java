package com.cedricziel.idea.typo3.extbase.controller;

import com.intellij.openapi.util.TextRange;

import java.io.Serializable;
import java.util.Objects;

public class StubControllerAction implements ControllerActionInterface, Serializable {
    private String name;
    private String controllerName;
    private String extensionName;
    private String pluginName;
    private String controllerFQN;
    private TextRange textRange;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    @Override
    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StubControllerAction that = (StubControllerAction) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(controllerName, that.controllerName) &&
                Objects.equals(extensionName, that.extensionName) &&
                Objects.equals(pluginName, that.pluginName) &&
                Objects.equals(controllerFQN, that.controllerFQN) &&
                Objects.equals(textRange, that.textRange);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, controllerName, extensionName, pluginName, controllerFQN, textRange);
    }

    public void setControllerFQN(String controllerFQN) {
        this.controllerFQN = controllerFQN;
    }

    public void setTextRange(TextRange textRange) {
        this.textRange = textRange;
    }

    public TextRange getTextRange() {
        return textRange;
    }
}
