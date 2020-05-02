package com.cedricziel.idea.typo3.domain;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class TYPO3ServiceDefinition implements Serializable {

    @NotNull
    private final String id;
    private String extensionName;
    private String className;
    private String title;
    private String description;
    private String subType;
    private Boolean available;
    private Integer priority;
    private Integer quality;
    private String os;
    private String exec;
    private String signature;

    public TYPO3ServiceDefinition(@NotNull String id) {
        this.id = id;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public void setClass(String aClass) {
        this.className = aClass;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TYPO3ServiceDefinition that = (TYPO3ServiceDefinition) o;

        if (!id.equals(that.id)) return false;
        if (!Objects.equals(extensionName, that.extensionName))
            return false;
        if (!Objects.equals(className, that.className)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(description, that.description)) return false;
        if (!Objects.equals(subType, that.subType)) return false;
        if (!Objects.equals(available, that.available)) return false;
        if (!Objects.equals(priority, that.priority)) return false;
        if (!Objects.equals(quality, that.quality)) return false;
        if (!Objects.equals(os, that.os)) return false;
        if (!Objects.equals(exec, that.exec)) return false;
        return Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (extensionName != null ? extensionName.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (subType != null ? subType.hashCode() : 0);
        result = 31 * result + (available != null ? available.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (exec != null ? exec.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        return result;
    }
}
