package com.cedricziel.idea.typo3.domain;

import com.intellij.psi.PsiElement;

public class TYPO3RouteDefinition {
    private String name;
    private String path;
    private String access;
    private String target;
    private String type;
    private PsiElement element;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setElement(PsiElement element) {
        this.element = element;
    }

    public PsiElement getElement() {
        return element;
    }
}
