package com.cedricziel.idea.typo3.index;

import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class ResourcePathIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.resource_path");

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            Map<String, Void> map = new THashMap<>();

            map.put(compileId(inputData), null);

            return map;
        };
    }

    private String compileId(FileContent inputData) {
        String path = inputData.getFile().getPath();
        String filePosition = "";
        if (path.contains("typo3conf/ext/")) {
            filePosition = path.split("typo3conf/ext/")[1];
        }
        if (path.contains("sysext/")) {
            filePosition = path.split("sysext/")[1];
        }

        return "EXT:" + filePosition;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.isInLocalFileSystem() && (file.getPath().contains("sysext") ||file.getPath().contains("typo3conf/ext"));
    }

    @Override
    public boolean dependsOnFileContent() {
        return false;
    }

    public static Collection<String> getAvailableExtensionResourceFiles(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project);
    }

    public static boolean projectContainsResourceFile(@NotNull Project project, @NotNull String resourceId) {
        return FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project).contains(resourceId);
    }

    public static boolean projectContainsResourceDirectory(@NotNull Project project, @NotNull String resourceId) {
        return false;
    }
}
