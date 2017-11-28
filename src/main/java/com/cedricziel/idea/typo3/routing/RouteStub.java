package com.cedricziel.idea.typo3.routing;

import java.io.Serializable;

public class RouteStub implements RouteInterface, Serializable {
    private String name;
    private String path;
    private String controller;
    private String method;

    public RouteStub() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteStub routeStub = (RouteStub) o;

        if (name != null ? !name.equals(routeStub.name) : routeStub.name != null) return false;
        if (path != null ? !path.equals(routeStub.path) : routeStub.path != null) return false;
        if (controller != null ? !controller.equals(routeStub.controller) : routeStub.controller != null) return false;
        return method != null ? method.equals(routeStub.method) : routeStub.method == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
