package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class JavaScriptUtil {

    public static final String MODULE_PREFIX = "TYPO3/CMS/";
    public static final String SIGNIFICANT_PATH = "Resources/Public/JavaScript/";
    public static final Key<String> MODULE_NAME_DATA_KEY = Key.create("t3module");
    public static final Key<CachedValue<Map<String, List<PsiFile>>>> MODULE_MAP = Key.create("t3modulemap");

    @NotNull
    public static Map<String, List<PsiFile>> getModuleMap(@NotNull Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(
            project,
            MODULE_MAP,
            () -> CachedValueProvider.Result.create(doGetModuleMap(project), PsiModificationTracker.MODIFICATION_COUNT),
            false
        );
    }

    @NotNull
    public static Map<String, List<PsiFile>> doGetModuleMap(@NotNull Project project) {
        Map<String, List<PsiFile>> map = new HashMap<>();
        for (PsiFile psiFile : findModuleFiles(project)) {
            if (psiFile.getUserData(MODULE_NAME_DATA_KEY) != null) {
                map.put(psiFile.getUserData(MODULE_NAME_DATA_KEY), Collections.singletonList(psiFile));

                continue;
            }

            String name = calculateModuleName(psiFile);
            if (name != null) {
                psiFile.putUserData(MODULE_NAME_DATA_KEY, name);
                map.put(name, Collections.singletonList(psiFile));
            }
        }

        return map;
    }

    @NotNull
    public static List<PsiFile> findModuleFilesFor(@NotNull Project project, @NotNull String identifier) {

        return getModuleMap(project).getOrDefault(identifier, Collections.emptyList());
    }

    @NotNull
    public static List<PsiFile> findModuleFiles(@NotNull Project project) {

        return FilenameIndex
            .getAllFilesByExt(project, "js", GlobalSearchScope.projectScope(project))
            .stream()
            .filter(f -> f.getPath().contains(SIGNIFICANT_PATH))
            .map(f -> PsiManager.getInstance(project).findFile(f))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public static String calculateModuleName(@NotNull PsiFile file) {
        String extensionKey = ExtensionUtility.findExtensionKeyFromFile(file);
        if (extensionKey == null) {
            return null;
        }

        VirtualFile virtualFile = file.getVirtualFile();
        String modLocalName = virtualFile.getPath();
        String nameWithoutExtension = virtualFile.getNameWithoutExtension();

        String[] pathSplit = modLocalName.split(SIGNIFICANT_PATH);
        if (pathSplit.length <= 1) {
            return null;
        }

        String[] nameSplit = pathSplit[1].split(nameWithoutExtension);
        if (nameSplit.length <= 1) {
            return null;
        }

        return MODULE_PREFIX + normalizeExtensionKeyForJs(extensionKey) + "/" + nameSplit[0] + nameWithoutExtension;
    }

    @NotNull
    public static String normalizeExtensionKeyForJs(@NotNull String extensionKey) {
        return WordUtils.capitalizeFully(extensionKey, new char[]{'_'}).replaceAll("_", "");
    }
}
