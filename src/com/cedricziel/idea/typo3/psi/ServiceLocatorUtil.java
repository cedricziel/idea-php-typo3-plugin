package com.cedricziel.idea.typo3.psi;

import com.cedricziel.idea.typo3.index.CoreServiceMapStubIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.*;
import java.util.logging.Logger;

public class ServiceLocatorUtil {
    public static void find(String id, Project project) {
        List<Set<String>> values = FileBasedIndex.getInstance().getValues(CoreServiceMapStubIndex.KEY, id, GlobalSearchScope.allScope(project));
        Logger lf = Logger.getLogger("lf");
        lf.warning("Hello");
    }
}
