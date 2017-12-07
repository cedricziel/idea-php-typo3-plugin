package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.index.extensionScanner.MethodArgumentDroppedIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtensionScannerUtil {
    public static boolean classMethodHasDroppedArguments(@NotNull Project project, @NotNull String compiledClassMethodKey) {

        return FileBasedIndex.getInstance().getAllKeys(MethodArgumentDroppedIndex.KEY, project).contains(compiledClassMethodKey);
    }

    public static int getMaximumNumberOfArguments(@NotNull Project project, @NotNull String compiledClassMethodKey) {
        List<Integer> values = FileBasedIndex.getInstance().getValues(MethodArgumentDroppedIndex.KEY, compiledClassMethodKey, GlobalSearchScope.allScope(project));

        if (values.size() == 0) {
            return -1;
        }

        return values.iterator().next();
    }
}
