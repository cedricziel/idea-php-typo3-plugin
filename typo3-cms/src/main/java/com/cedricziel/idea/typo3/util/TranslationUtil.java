package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.cedricziel.idea.typo3.translation.TranslationLookupElement;
import com.cedricziel.idea.typo3.translation.TranslationReference;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        return possibleKey.startsWith("LLL:");
    }

    public static String extractResourceFilenameFromTranslationString(@NotNull String contents) {

        String[] split = contents.replace("LLL:EXT:", "").split("/");
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

    @NotNull
    public static TranslationLookupElement[] createLookupElements(@NotNull Project project) {
        return TranslationIndex
                .findAllTranslationStubs(project)
                .parallelStream()
                .map(TranslationLookupElement::new)
                .toArray(TranslationLookupElement[]::new);
    }

    public static PsiElement[] findDefinitionElements(@NotNull Project project, @NotNull String translationId) {
        Set<String> keys = new HashSet<>();
        keys.add(translationId);

        List<PsiElement> elements = new ArrayList<>();
        FileBasedIndex.getInstance().getFilesWithKey(TranslationIndex.KEY, keys, virtualFile -> {
            FileBasedIndex.getInstance().processValues(TranslationIndex.KEY, translationId, virtualFile, (file, value) -> {
                PsiFile file1 = PsiManager.getInstance(project).findFile(file);
                if (file1 != null) {
                    PsiElement elementAt = file1.findElementAt(value.getTextRange().getStartOffset());
                    if (elementAt != null) {
                        if (elementAt.getParent() instanceof XmlTag) {
                            XmlAttribute id = ((XmlTag) elementAt.getParent()).getAttribute("id");
                            if (id == null) {
                                return true;
                            }

                            elements.add(id.getValueElement());

                            return true;
                        }
                        elements.add(elementAt.getParent());
                    }
                }

                return true;
            }, GlobalSearchScope.allScope(project));

            return true;
        }, GlobalSearchScope.allScope(project));

        return elements.toArray(new PsiElement[elements.size()]);
    }

    public static boolean hasTranslationReference(@NotNull PsiElement element) {

        for (PsiReference reference : element.getReferences()) {
            if (reference instanceof TranslationReference) {
                return true;
            }
        }

        return false;
    }

    public static TranslationReference getTranslationReference(@NotNull PsiElement element) {

        for (PsiReference reference : element.getReferences()) {
            if (reference instanceof TranslationReference) {
                return (TranslationReference) reference;
            }
        }

        return null;
    }

    public static String keyFromReference(String key) {
        return StringUtils.substringAfterLast(key, ":");
    }
}