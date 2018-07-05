package com.cedricziel.idea.typo3.routing;

import com.intellij.openapi.util.TextRange;

import java.io.Serializable;
import java.util.Objects;

public class RouteStub implements RouteInterface, Serializable {
    private String name;
    private String path;
    private String controller;
    private String method;
    private String access;
    private TextRange textRange;

    public RouteStub() {
        access = "private";
    }

    public RouteStub(String name, String path, String controller, String method) {
        this.name = name;
        this.path = path;
        this.controller = controller;
        this.method = method;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(TextRange textRange) {
        this.textRange = textRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteStub routeStub = (RouteStub) o;
        return Objects.equals(name, routeStub.name) &&
                Objects.equals(path, routeStub.path) &&
                Objects.equals(controller, routeStub.controller) &&
                Objects.equals(method, routeStub.method) &&
                Objects.equals(access, routeStub.access) &&
                Objects.equals(textRange, routeStub.textRange);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, path, controller, method, access, textRange);
    }
}
