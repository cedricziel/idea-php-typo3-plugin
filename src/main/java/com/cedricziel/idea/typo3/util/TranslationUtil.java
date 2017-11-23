package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

public class TranslationUtil {
    public static boolean keyExists(@NotNull Project project, @NotNull String key) {
        return FileBasedIndex
                .getInstance()
                .getAllKeys(TranslationIndex.KEY, project)
                .contains(key);
    }

    public static boolean isTranslationKeyString(@NotNull String possibleKey) {
        if (possibleKey.isEmpty()) {
            return false;
        }

        return possibleKey.contains("LLL:");
    }

    public static String extractResourceFilenameFromTranslationString(@NotNull String contents) {

        String[] split = contents.replace("LLL:", "").split(":");
        if (split.length > 0) {
            return split[0];
        }

        return null;
    }

    public static String extractTranslationKeyTranslationString(@NotNull String contents) {

        contents = contents.replace("LLL:", "");
        String[] split = contents.split(":");
        if (split.length > 0) {
            return split[1];
        }

        return null;
    }
}
