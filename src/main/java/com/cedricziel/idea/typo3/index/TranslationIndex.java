package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.translation.StubTranslation;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.cedricziel.idea.typo3.util.FilesystemUtil;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TranslationIndex extends FileBasedIndexExtension<String, StubTranslation> {

    public static final ID<String, StubTranslation> KEY = ID.create("com.cedricziel.idea.typo3.index.translation_key");

    private final DataIndexer<String, StubTranslation, FileContent> myIndexer = new DataIndexer<String, StubTranslation, FileContent>() {
        @Override
        @NotNull
        public Map<String, StubTranslation> map(@NotNull FileContent inputData) {
            Language language = ((LanguageFileType) inputData.getFileType()).getLanguage();
            String extension = inputData.getFile().getExtension();

            String extensionKeyFromFile = ExtensionUtility.findExtensionKeyFromFile(inputData.getFile());
            if (extensionKeyFromFile == null) {
                return new HashMap<>();
            }

            String languageKey = extractLanguageKeyFromFile(inputData);

            if (language instanceof XMLLanguage && extension != null && extension.equals("xlf")) {
                PsiFile psiFile = inputData.getPsiFile();
                Map<String, StubTranslation> result = new HashMap<>();

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
                                                            for (String calculatedId : compileIds(inputData, extensionKeyFromFile, id)) {
                                                                StubTranslation v = createStubTranslationFromIndex(inputData, extensionKeyFromFile, languageKey, transUnitElement, id);
                                                                result.put(calculatedId, v);
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
                }

                return result;
            }

            if (language == XMLLanguage.INSTANCE && extension != null && extension.equals("xml")) {
                PsiFile psiFile = inputData.getPsiFile();
                Map<String, StubTranslation> result = new HashMap<>();

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
                                                            for (String calculatedId : compileIds(inputData, extensionKeyFromFile, id)) {
                                                                StubTranslation v = createStubTranslationFromIndex(inputData, extensionKeyFromFile, languageKey, transUnitElement, id);
                                                                result.put(calculatedId, v);
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
                }

                return result;
            }

            return Collections.emptyMap();
        }
    };

    @NotNull
    public static Collection<String> findAllTranslations(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, project);
    }

    @NotNull
    public static List<StubTranslation> findAllTranslationStubs(@NotNull Project project) {
        return findAllTranslations(project).stream().map(id -> findById(project, id)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<StubTranslation> findById(@NotNull Project project, @NotNull String id) {
        return FileBasedIndex.getInstance().getValues(TranslationIndex.KEY, id, GlobalSearchScope.allScope(project));
    }

    private String[] compileIds(FileContent inputData, String extensionKeyFromFile, String id) {
        String languageKey = extractLanguageKeyFromFile(inputData);

        VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(inputData.getFile());

        String path = inputData.getFile().getPath();
        String filePosition = extensionKeyFromFile + path.split(extensionRootFolder.getPath())[1];

        if (!languageKey.equals("en")) {
            filePosition = filePosition.replace(languageKey + ".", "");
        }

        String fileBasedId = compileId(inputData, extensionKeyFromFile, id);
        String s = "LLL:EXT:" + filePosition + ":" + id;

        if (fileBasedId.equals(s)) {
            return new String[]{fileBasedId};
        }

        return new String[]{fileBasedId, s};
    }

    private String extractLanguageKeyFromFile(FileContent inputData) {
        String languageKey = "en";
        String nameWithoutExtension = inputData.getFile().getNameWithoutExtension();
        if (nameWithoutExtension.indexOf(".") == 2) {
            String[] split = nameWithoutExtension.split(Pattern.quote("."));
            if (split.length != 0) {
                languageKey = split[0];
            }
        }
        return languageKey;
    }

    @NotNull
    private StubTranslation createStubTranslationFromIndex(@NotNull FileContent inputData, String extensionKeyFromFile, String languageKey, PsiElement transUnitElement, String id) {
        StubTranslation v = new StubTranslation(compileId(inputData, extensionKeyFromFile, id));
        v.setTextRange(transUnitElement.getTextRange());
        v.setIndex(id);
        v.setExtension(extensionKeyFromFile);
        v.setLanguage(languageKey);
        return v;
    }

    private String compileId(FileContent inputData, String extensionKeyFromFile, String id) {

        VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(inputData.getFile());

        String path = inputData.getFile().getPath();
        String filePosition = extensionKeyFromFile + path.split(extensionRootFolder.getPath())[1];

        return "LLL:EXT:" + filePosition + ":" + id;
    }

    @NotNull
    @Override
    public ID<String, StubTranslation> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, StubTranslation, FileContent> getIndexer() {
        return myIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataExternalizer<StubTranslation> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
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
        return 3;
    }
}
