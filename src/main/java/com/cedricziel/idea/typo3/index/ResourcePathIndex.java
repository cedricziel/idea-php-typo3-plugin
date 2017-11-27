package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.util.FilesystemUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.cedricziel.idea.typo3.util.ComposerUtil.findExtensionKey;

public class ResourcePathIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.resource_path");

    public static Collection<String> getAvailableExtensionResourceFiles(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project);
    }

    public static boolean projectContainsResourceFile(@NotNull Project project, @NotNull String resourceId) {
        return FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project).contains(resourceId);
    }

    public static boolean projectContainsResourceDirectory(@NotNull Project project, @NotNull String resourceId) {
        Set<String> keys = new HashSet<>();
        keys.add(StringUtils.strip(resourceId, "/"));

        Set<VirtualFile> matches = new HashSet<>();

        FileBasedIndex.getInstance().getFilesWithKey(ResourcePathIndex.KEY, keys, file -> {
            if (file.isDirectory()) {

                matches.add(file);

                return false;
            }

            return true;
        }, GlobalSearchScope.allScope(project));

        return matches.size() > 0;
    }

    public static PsiElement[] findElementsForKey(@NotNull Project project, @NotNull String identifier) {
        Set<String> keys = new HashSet<>();
        keys.add(identifier);
        Set<PsiElement> elements = new HashSet<>();

        FileBasedIndex.getInstance().getFilesWithKey(ResourcePathIndex.KEY, keys, virtualFile -> {
            elements.add(PsiManager.getInstance(project).findFile(virtualFile));

            return true;
        }, GlobalSearchScope.allScope(project));

        return elements
                .stream()
                .filter(Objects::nonNull)
                .toArray(PsiElement[]::new);
    }

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

            String path = inputData.getFile().getPath();
            if (path.contains("sysext") || path.contains("typo3conf/ext")) {
                map.put(compileId(inputData), null);

                return map;
            }

            VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(inputData.getFile());
            if (extensionRootFolder != null) {
                // 1. try to read sibling composer.json
                VirtualFile composerJsonFile = extensionRootFolder.findChild("composer.json");
                if (composerJsonFile != null) {
                    String extensionKey = findExtensionKey(composerJsonFile);
                    if (extensionKey != null) {
                        map.put(compileId(extensionRootFolder, extensionKey, inputData.getFile()), null);
                        return map;
                    }
                }

                // 2. try to infer from directory name
                map.put(compileId(extensionRootFolder.getName(), extensionRootFolder.getPath(), inputData.getFile()), null);
            }

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

    private String compileId(String extensionKey, String directoryPath, VirtualFile file) {

        return "EXT:" + extensionKey + file.getPath().replace(directoryPath, "");
    }

    private String compileId(VirtualFile extensionRootDirectory, String extensionKey, VirtualFile file) {

        return "EXT:" + extensionKey + file.getPath().replace(extensionRootDirectory.getPath(), "");
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return VirtualFile::isInLocalFileSystem;
    }

    @Override
    public boolean dependsOnFileContent() {
        return false;
    }
}
