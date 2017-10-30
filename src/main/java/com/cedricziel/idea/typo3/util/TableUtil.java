package com.cedricziel.idea.typo3.util;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableUtil {

    public static final String EXT_TABLES_SQL_FILENAME = "ext_tables.sql";

    public static final String[] TCA_TABLE_FIELDS = {
            "foreign_table",
            "allowed",
    };

    public static Set<String> getAvailableTableNames(@NotNull Project project) {
        PsiFile[] extSqlFiles = FilenameIndex.getFilesByName(project, EXT_TABLES_SQL_FILENAME, GlobalSearchScope.allScope(project));

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
