package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.index.TablenameFileIndex;
import com.cedricziel.idea.typo3.tca.TableLookupElement;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TableUtil {

    public static final String EXT_TABLES_SQL_FILENAME = "ext_tables.sql";

    public static Set<String> getAvailableTableNames(@NotNull Project project) {

        return new HashSet<>(FileBasedIndex.getInstance().getAllKeys(TablenameFileIndex.KEY, project));
    }

    public static PsiElement[] getTableDefinitionElements(@NotNull String tableName, @NotNull Project project) {

        PsiFile[] extTablesSqlFilesInProjectContainingTable = getExtTablesSqlFilesInProjectContainingTable(tableName, project);
        Set<PsiElement> elements = new HashSet<>();

        PsiManager psiManager = PsiManager.getInstance(project);

        for (PsiFile virtualFile : extTablesSqlFilesInProjectContainingTable) {
            FileBasedIndex.getInstance().processValues(TablenameFileIndex.KEY, tableName, virtualFile.getVirtualFile(), (file, value) -> {

                PsiFile file1 = psiManager.findFile(file);
                if (file1 != null) {
                    PsiElement elementAt = file1.findElementAt(value.getEndOffset() - 2);
                    if (elementAt != null) {
                        elements.add(elementAt);
                    }
                }

                return true;
            }, GlobalSearchScope.allScope(project));
        }

        return elements.toArray(new PsiElement[elements.size()]);
    }

    private static PsiFile[] getExtTablesSqlFilesInProjectContainingTable(@NotNull String tableName, @NotNull Project project) {
        Set<String> set = new HashSet<>();
        set.add(tableName);

        Set<PsiFile> files = new HashSet<>();
        PsiManager psiManager = PsiManager.getInstance(project);

        FileBasedIndex.getInstance().getFilesWithKey(TablenameFileIndex.KEY, set, virtualFile -> {

            files.add(psiManager.findFile(virtualFile));

            return true;
        }, GlobalSearchScope.allScope(project));

        return files.toArray(new PsiFile[files.size()]);
    }

    @NotNull
    private static PsiFile[] getExtTablesSqlFilesInProject(@NotNull Project project) {
        return FilenameIndex.getFilesByName(project, EXT_TABLES_SQL_FILENAME, GlobalSearchScope.allScope(project));
    }

    public static void completeAvailableTableNames(@NotNull Project project, @NotNull CompletionResultSet completionResultSet) {
        for (String name : TableUtil.getAvailableTableNames(project)) {
            completionResultSet.addElement(new LookupElement() {
                @NotNull
                @Override
                public String getLookupString() {

                    return name;
                }
            });
        }
    }

    @NotNull
    public static LookupElement[] createAvailableTableNamesLookupElements(@NotNull Project project) {
        Collection<LookupElement> elements = new HashSet<>();
        for (String name : TableUtil.getAvailableTableNames(project)) {
            elements.add(new TableLookupElement(name));
        }

        return elements.toArray(new LookupElement[elements.size()]);
    }
}
