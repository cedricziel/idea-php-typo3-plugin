package com.cedricziel.idea.typo3.domain;

import com.intellij.openapi.vfs.VirtualFile;

public class TYPO3ExtensionDefinition {

    public static final String COMPOSER_TYPE_CMS_FRAMEWORK = "typo3-cms-framework";

    public static final String COMPOSER_TYPE_CMS_EXTENSION = "typo3-cms-extension";

    public static final String COMPOSER_TYPE_LIBRARY = "library";

    private VirtualFile rootDirectory;

    private String extensionKey;

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getExtensionKey() {
        return extensionKey;
    }

    public void setExtensionKey(String extensionKey) {
        this.extensionKey = extensionKey;
    }

    public VirtualFile getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(VirtualFile rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
