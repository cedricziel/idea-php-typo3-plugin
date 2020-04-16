package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.util.FilesystemUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.cedricziel.idea.typo3.util.ComposerUtil.findExtensionKey;

public class ResourcePathIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.resource_path");

    private static final Key<CachedValue<Collection<String>>> RESOURCE_KEYS = new Key<>("TYPO3_CMS_RESOURCE_KEYS");

    private static final ConcurrentMap<Project, Collection<String>> RESOURCE_KEYS_LOCAL_CACHE = new ConcurrentHashMap<>();

    public static Collection<String> getAvailableExtensionResourceFiles(@NotNull Project project) {
        return getAllResourceKeys(project);
    }

    public static boolean projectContainsResourceFile(@NotNull Project project, @NotNull String resourceId) {
        return getAllResourceKeys(project).contains(resourceId);
    }

    @NotNull
    private synchronized static Collection<String> getAllResourceKeys(@NotNull Project project) {
        CachedValue<Collection<String>> userData = project.getUserData(RESOURCE_KEYS);
        if (userData != null && userData.hasUpToDateValue()) {
            return RESOURCE_KEYS_LOCAL_CACHE.getOrDefault(project, new ArrayList<>());
        }

        CachedValue<Collection<String>> cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
            Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project);
            if (RESOURCE_KEYS_LOCAL_CACHE.containsKey(project)) {
                RESOURCE_KEYS_LOCAL_CACHE.replace(project, allKeys);
            } else {
                RESOURCE_KEYS_LOCAL_CACHE.put(project, allKeys);
            }

            return CachedValueProvider.Result.create(new ArrayList<>(), PsiModificationTracker.MODIFICATION_COUNT);
        }, false);
        project.putUserData(RESOURCE_KEYS, cachedValue);

        return RESOURCE_KEYS_LOCAL_CACHE.getOrDefault(project, cachedValue.getValue());
    }

    public static boolean projectContainsResourceDirectory(@NotNull Project project, @NotNull String resourceId) {
        String interpolatedKey = StringUtils.strip(resourceId, "/") + "/";

        return FileBasedIndex.getInstance().getAllKeys(ResourcePathIndex.KEY, project)
            .parallelStream()
            .anyMatch(x -> x.startsWith(interpolatedKey));
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
                map.putAll(compileId(inputData));

                return map;
            }

            VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(inputData.getFile());
            if (extensionRootFolder != null) {
                // 1. try to read sibling composer.json
                VirtualFile composerJsonFile = extensionRootFolder.findChild("composer.json");
                if (composerJsonFile != null) {
                    String extensionKey = findExtensionKey(composerJsonFile);
                    if (extensionKey != null) {
                        map.putAll(compileId(extensionRootFolder, extensionKey, inputData.getFile()));
                        return map;
                    }
                }

                // 2. try to infer from directory name
                map.putAll(compileId(extensionRootFolder.getName(), extensionRootFolder.getPath(), inputData.getFile()));
            }

            return map;
        };
    }

    private Map<String, Void> compileId(FileContent inputData) {
        Map<String, Void> map = new HashMap<>();
        String path = inputData.getFile().getPath();
        String filePosition = "";
        if (path.contains("typo3conf/ext/")) {
            filePosition = path.split("typo3conf/ext/")[1];
        }
        if (path.contains("sysext/")) {
            filePosition = path.split("sysext/")[1];
        }

        String primaryKey = "EXT:" + filePosition;

        putSecondaryKeyIfNeeded(map, primaryKey);

        return map;
    }

    private void putSecondaryKeyIfNeeded(Map<String, Void> map, String primaryKey) {
        map.put(primaryKey, null);
        if (primaryKey.endsWith(".xlf") || primaryKey.endsWith(".xml")) {
            map.put("LLL:" + primaryKey, null);
        }
    }

    private Map<String, Void> compileId(String extensionKey, String directoryPath, VirtualFile file) {
        Map<String, Void> map = new HashMap<>();
        String primaryKey = "EXT:" + extensionKey + file.getPath().replace(directoryPath, "");

        putSecondaryKeyIfNeeded(map, primaryKey);

        return map;
    }

    private Map<String, Void> compileId(VirtualFile extensionRootDirectory, String extensionKey, VirtualFile file) {
        Map<String, Void> map = new HashMap<>();
        String primaryKey = "EXT:" + extensionKey + file.getPath().replace(extensionRootDirectory.getPath(), "");

        putSecondaryKeyIfNeeded(map, primaryKey);

        return map;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 2;
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
