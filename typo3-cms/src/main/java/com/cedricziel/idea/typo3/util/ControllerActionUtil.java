package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.extbase.controller.ControllerActionLookupElement;
import com.cedricziel.idea.typo3.index.extbase.ControllerActionIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ControllerActionUtil {
    public static ControllerActionLookupElement[] createLookupElements(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(ControllerActionIndex.KEY, project)
                .stream()
                .map(x -> FileBasedIndex.getInstance().getValues(ControllerActionIndex.KEY, x, GlobalSearchScope.allScope(project)))
                .flatMap(Collection::stream)
                .map(ControllerActionLookupElement::new)
                .toArray(ControllerActionLookupElement[]::new);
    }

    public static PsiElement[] getDefinitionElements(@NotNull Project project, @NotNull String actionName) {
        Set<String> keys = new HashSet<>();
        keys.add(actionName);

        List<PsiElement> elements = new ArrayList<>();
        FileBasedIndex.getInstance().getFilesWithKey(ControllerActionIndex.KEY, keys, virtualFile -> {
            FileBasedIndex.getInstance().processValues(ControllerActionIndex.KEY, actionName, virtualFile, (file, value) -> {
                PsiFile file1 = PsiManager.getInstance(project).findFile(file);
                if (file1 != null) {
                    PsiElement elementAt = file1.findElementAt(value.getTextRange().getStartOffset());
                    if (elementAt != null) {
                        elements.add(elementAt.getParent().getParent());
                    }
                }

                return true;
            }, GlobalSearchScope.allScope(project));

            return true;
        }, GlobalSearchScope.allScope(project));

        return elements.toArray(PsiElement.EMPTY_ARRAY);
    }
}
