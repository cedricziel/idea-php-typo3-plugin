package com.cedricziel.idea.typo3.index;

import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class TranslationIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.translation_key");

    private final DataIndexer<String, Void, FileContent> myIndexer = new DataIndexer<String, Void, FileContent>() {
        @Override
        @NotNull
        public Map<String, Void> map(@NotNull FileContent inputData) {
            Language language = ((LanguageFileType) inputData.getFileType()).getLanguage();
            String extension = inputData.getFile().getExtension();

            if (language instanceof XMLLanguage && extension != null && extension.equals("xlf")) {
                PsiFile psiFile = inputData.getPsiFile();
                Map<String, Void> result = new HashMap<>();

                for (PsiElement element : psiFile.getChildren()) {
                    if (PlatformPatterns.psiElement(XmlElementType.XML_DOCUMENT).accepts(element)) {
                        for (PsiElement xliffElement : element.getChildren()) {
                            if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("xliff").accepts(xliffElement)) {
                                for (PsiElement fileElement : xliffElement.getChildren()) {
                                    if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("file").accepts(fileElement)) {
                                        for (PsiElement bodyElement : fileElement.getChildren()) {
                                            if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("body").accepts(bodyElement)) {
                                                for (PsiElement transUnitElement : bodyElement.getChildren()) {
                                                    if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("trans-unit").accepts(transUnitElement)) {
                                                        if (transUnitElement instanceof XmlTag) {
                                                            String id = ((XmlTag) transUnitElement).getAttributeValue("id");
                                                            result.put(compileId(inputData, id), null);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return result;
            }

            if (language == XMLLanguage.INSTANCE && extension != null && extension.equals("xml")) {
                PsiFile psiFile = inputData.getPsiFile();
                Map<String, Void> result = new HashMap<>();

                for (PsiElement element : psiFile.getChildren()) {
                    if (PlatformPatterns.psiElement(XmlElementType.XML_DOCUMENT).accepts(element)) {
                        for (PsiElement xliffElement : element.getChildren()) {
                            if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("T3locallang").accepts(xliffElement)) {
                                for (PsiElement fileElement : xliffElement.getChildren()) {
                                    if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("data").accepts(fileElement)) {
                                        for (PsiElement bodyElement : fileElement.getChildren()) {
                                            if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("languageKey").accepts(bodyElement)) {
                                                for (PsiElement transUnitElement : bodyElement.getChildren()) {
                                                    if (PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("label").accepts(transUnitElement)) {
                                                        if (transUnitElement instanceof XmlTag) {
                                                            String id = ((XmlTag) transUnitElement).getAttributeValue("index");
                                                            result.put(compileId(inputData, id), null);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return result;
            }

            return Collections.emptyMap();
        }
    };

    private String compileId(FileContent inputData, String id) {
        String path = inputData.getFile().getPath();
        String filePosition = "";
        if (path.contains("typo3conf/ext/")) {
            filePosition = path.split("typo3conf/ext/")[1];
        }
        if (path.contains("sysext/")) {
            filePosition = path.split("sysext/")[1];
        }

        return "LLL:EXT:" + filePosition + ":" + id;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            String extension = file.getExtension();
            if (file.isInLocalFileSystem() && extension != null && (extension.equalsIgnoreCase("xml") || extension.equalsIgnoreCase("xlf"))) {
                return true;
            }

            return false;
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
