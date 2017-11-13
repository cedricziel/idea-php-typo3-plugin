package com.cedricziel.idea.typo3.util;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableUtil {

    public static final String EXT_TABLES_SQL_FILENAME = "ext_tables.sql";

    public static Set<String> getAvailableTableNames(@NotNull Project project) {
        PsiFile[] extSqlFiles = getExtTablesSqlFilesInProject(project);

        Set<String> tableNames = new HashSet<>();

        for (PsiFile psiFile : extSqlFiles) {

            if (psiFile != null) {

                CharSequence charSequence = LoadTextUtil.loadText(psiFile.getVirtualFile());

                final Matcher matcher = Pattern
                        .compile("create\\s+table\\s+(if\\s+not\\s+exists\\s+)?([a-zA-Z_0-9]+)", Pattern.CASE_INSENSITIVE)
                        .matcher(charSequence);

                try {
                    while (matcher.find()) {
                        if (matcher.groupCount() < 2) {
                            return tableNames;
                        }

                        tableNames.add(matcher.group(2));
                    }
                } catch (IllegalStateException e) {
                    // no matches
                }
            }
        }

        return tableNames;
    }

    public static PsiElement[] getTableDefinitionElements(@NotNull String tableName, @NotNull Project project) {

        PsiFile[] extTablesSqlFilesInProjectContainingTable = getExtTablesSqlFilesInProjectContainingTable(tableName, project);

        return Arrays
                .stream(extTablesSqlFilesInProjectContainingTable)
                .map(file -> {
                    CharSequence charSequence = LoadTextUtil.loadText(file.getVirtualFile());

                    final Matcher matcher = Pattern
                            .compile("create\\s+table\\s+(if\\s+not\\s+exists\\s+)?([a-zA-Z_0-9]+)", Pattern.CASE_INSENSITIVE)
                            .matcher(charSequence);
                    try {
                        while (matcher.find()) {
                            if (matcher.groupCount() < 2) {
                                continue;
                            }

                            final String foundTable = matcher.group(2);
                            if (!foundTable.equals(tableName)) {
                                continue;
                            }

                            return file.findElementAt(matcher.end() - 2);
                        }
                    } catch (IllegalStateException e) {
                        // do nothing
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(PsiElement[]::new);
    }

    private static PsiFile[] getExtTablesSqlFilesInProjectContainingTable(@NotNull String tableName, @NotNull Project project) {

        return Arrays
                .stream(getExtTablesSqlFilesInProject(project))
                .filter(Objects::nonNull)
                .filter(file -> LoadTextUtil.loadText(file.getVirtualFile()).toString().contains(tableName))
                .toArray(PsiFile[]::new);
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
}
