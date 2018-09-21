package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.translation.StubTranslation;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.cedricziel.idea.typo3.util.FilesystemUtil;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TranslationIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.translation_key");

    private final DataIndexer<String, Void, FileContent> myIndexer = inputData -> {
        // covers the case where no FileType association has been made yet
        if (inputData.getFileType() instanceof UnknownFileType) {
            return Collections.emptyMap();
        }

        Language language = ((LanguageFileType) inputData.getFileType()).getLanguage();
        String extension = inputData.getFile().getExtension();

        String extensionKeyFromFile = ExtensionUtility.findExtensionKeyFromFile(inputData.getFile());
        if (extensionKeyFromFile == null) {
            return Collections.emptyMap();
        }

        String languageKey = extractLanguageKeyFromFile(inputData);

        HashMap<String, Void> returnResult = new HashMap<>();
        if (language instanceof XMLLanguage && extension != null && extension.equals("xlf")) {
            PsiFile psiFile = inputData.getPsiFile();

            XLIFFTranslationVisitor visitor = new XLIFFTranslationVisitor(languageKey, extensionKeyFromFile, inputData.getFile());

            psiFile.accept(visitor);

            visitor.result.keySet().forEach(k -> returnResult.put(k, null));

            return returnResult;
        }

        if (language == XMLLanguage.INSTANCE && extension != null && extension.equals("xml")) {
            PsiFile psiFile = inputData.getPsiFile();

            XMLTranslationVisitor visitor = new XMLTranslationVisitor(languageKey, extensionKeyFromFile, inputData.getFile());

            psiFile.accept(visitor);

            visitor.result.keySet().forEach(k -> returnResult.put(k, null));

            return returnResult;
        }

        return returnResult;
    };

    @NotNull
    public static Collection<String> findAllKeys(@NotNull Project project) {
        return new HashSet<>(FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, project));
    }

    @NotNull
    public static List<StubTranslation> findAllTranslationStubs(@NotNull Project project) {
        return findAllKeys(project).stream().map(id -> findById(project, id)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<StubTranslation> findById(@NotNull Project project, @NotNull String id) {
        Set<StubTranslation> stubSet = new HashSet<>();

        FileBasedIndex.getInstance().getFilesWithKey(
            TranslationIndex.KEY,
            ContainerUtil.set(id),
            v -> {
                String languageKey = extractLanguageKeyFromFile(v);
                PsiFile psiFile = PsiManager.getInstance(project).findFile(v);
                if (psiFile == null) {
                    return true;
                }

                if (psiFile.getLanguage() instanceof XMLLanguage && v.getExtension() != null && v.getExtension().equals("xlf")) {
                    XLIFFTranslationVisitor visitor = new XLIFFTranslationVisitor(languageKey, ExtensionUtility.findExtensionKeyFromFile(v), v);

                    psiFile.accept(visitor);

                    visitor.result.keySet().forEach(k -> stubSet.addAll(visitor.result.values().stream().flatMap(Collection::stream).collect(Collectors.toList())));
                }


                if (psiFile.getLanguage() == XMLLanguage.INSTANCE && v.getExtension() != null && v.getExtension().equals("xml")) {
                    XMLTranslationVisitor visitor = new XMLTranslationVisitor(languageKey, ExtensionUtility.findExtensionKeyFromFile(v), v);

                    psiFile.accept(visitor);

                    visitor.result.keySet().forEach(k -> stubSet.addAll(visitor.result.values().stream().flatMap(Collection::stream).collect(Collectors.toList())));
                }

                return true;
            },
            GlobalSearchScope.allScope(project)
        );

        stubSet.removeIf(s -> !s.getIndex().equals(TranslationUtil.extractLocalKeyFromTranslationString(id)));

        return new ArrayList<>(stubSet);
    }

    private static String[] compileIds(VirtualFile file, String extensionKeyFromFile, String id) {
        String languageKey = extractLanguageKeyFromFile(file);

        VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(file);

        String path = file.getPath();
        String filePosition = extensionKeyFromFile + path.split(extensionRootFolder.getPath())[1];

        if (!languageKey.equals("en")) {
            filePosition = filePosition.replace(languageKey + ".", "");
        }

        String fileBasedId = compileId(file, extensionKeyFromFile, id);
        String s = "LLL:EXT:" + filePosition + ":" + id;

        if (fileBasedId.equals(s)) {
            return new String[]{fileBasedId};
        }

        return new String[]{fileBasedId, s};
    }

    private static String extractLanguageKeyFromFile(VirtualFile inputData) {
        String languageKey = "en";
        String nameWithoutExtension = inputData.getNameWithoutExtension();
        if (nameWithoutExtension.indexOf(".") == 2) {
            String[] split = nameWithoutExtension.split(Pattern.quote("."));
            if (split.length != 0) {
                languageKey = split[0];
            }
        }
        return languageKey;
    }

    @NotNull
    private static StubTranslation createStubTranslationFromIndex(@NotNull VirtualFile file, String extensionKeyFromFile, String languageKey, PsiElement transUnitElement, String id) {
        StubTranslation v = new StubTranslation(SmartPointerManager.createPointer(transUnitElement), compileId(file, extensionKeyFromFile, id));
        v.setTextRange(transUnitElement.getTextRange());
        v.setIndex(id);
        v.setExtension(extensionKeyFromFile);
        v.setLanguage(languageKey);
        return v;
    }

    private static String compileId(VirtualFile file, String extensionKeyFromFile, String id) {
        VirtualFile extensionRootFolder = FilesystemUtil.findExtensionRootFolder(file);

        String path = file.getPath();
        String filePosition = extensionKeyFromFile + path.split(extensionRootFolder.getPath())[1];

        return "LLL:EXT:" + filePosition + ":" + id;
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

            return file.isInLocalFileSystem() && extension != null && (extension.equalsIgnoreCase("xml") || extension.equalsIgnoreCase("xlf"));
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 4;
    }

    abstract static class AbstractTranslationVisitor extends XmlRecursiveElementVisitor {
        String languageKey;
        String extensionKeyFromFile;
        VirtualFile file;

        HashMap<String, ArrayList<StubTranslation>> result = new HashMap<>();

        AbstractTranslationVisitor(String languageKey, String extensionKey, VirtualFile file) {
            this.languageKey = languageKey;
            this.extensionKeyFromFile = extensionKey;
            this.file = file;
        }

        @Override
        public void visitXmlTag(XmlTag tag) {
            if (getElementPattern().accepts(tag)) {
                extractTranslationStub(tag);
            }

            super.visitXmlTag(tag);
        }

        abstract void extractTranslationStub(@NotNull XmlTag tag);

        @NotNull
        abstract PsiElementPattern.Capture<PsiElement> getElementPattern();
    }

    private static class XLIFFTranslationVisitor extends AbstractTranslationVisitor {

        XLIFFTranslationVisitor(String languageKey, String extensionKey, VirtualFile file) {
            super(languageKey, extensionKey, file);
        }

        void extractTranslationStub(@NotNull XmlTag tag) {
            String id = tag.getAttributeValue("id");
            String languageKeyToUse = String.valueOf(languageKey);

            XmlTag fileTag = (XmlTag) PsiTreeUtil.findFirstParent(tag, t -> PlatformPatterns.psiElement(XmlTag.class).withName("file").accepts(t));

            String sourceValue = "";
            String targetValue = "";
            if (fileTag != null) {
                if (fileTag.getAttributeValue("source-language") != null) {
                    sourceValue = fileTag.getAttributeValue("source-language");
                }

                if (fileTag.getAttributeValue("target-language") != null) {
                    targetValue = fileTag.getAttributeValue("target-language");
                }
            }

            if (!sourceValue.isEmpty() && !targetValue.isEmpty()) {
                languageKeyToUse = targetValue;
            }

            for (String calculatedId : compileIds(file, extensionKeyFromFile, id)) {
                if (result.containsKey(calculatedId)) {
                    result.get(calculatedId).add(createStubTranslationFromIndex(file, extensionKeyFromFile, languageKeyToUse, tag, id));
                } else {
                    ArrayList<StubTranslation> v = new ArrayList<>();
                    v.add(createStubTranslationFromIndex(file, extensionKeyFromFile, languageKeyToUse, tag, id));

                    result.put(calculatedId, v);
                }
            }
        }

        @NotNull
        PsiElementPattern.Capture<PsiElement> getElementPattern() {

            return PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("trans-unit").withParent(
                PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("body").withParent(
                    PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("file").withParent(
                        PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("xliff").withParent(
                            PlatformPatterns.psiElement(XmlElementType.XML_DOCUMENT)
                        )
                    )
                )
            );
        }
    }

    private static class XMLTranslationVisitor extends AbstractTranslationVisitor {
        XMLTranslationVisitor(String languageKey, String extensionKey, VirtualFile file) {
            super(languageKey, extensionKey, file);
        }

        @Override
        void extractTranslationStub(@NotNull XmlTag tag) {
            String id = tag.getAttributeValue("index");
            for (String calculatedId : compileIds(file, extensionKeyFromFile, id)) {
                XmlTag languageKeyTag = (XmlTag) PsiTreeUtil.findFirstParent(tag, t -> PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("languageKey").accepts(t));
                if (languageKeyTag != null && languageKeyTag.getAttributeValue("index") != null) {
                    if (result.containsKey(calculatedId)) {
                        result.get(calculatedId).add(createStubTranslationFromIndex(file, extensionKeyFromFile, languageKeyTag.getAttributeValue("index"), tag, id));
                    } else {
                        result.put(calculatedId, new ArrayList<StubTranslation>() {{
                            add(createStubTranslationFromIndex(file, extensionKeyFromFile, languageKeyTag.getAttributeValue("index"), tag, id));
                        }});
                    }
                } else {
                    if (result.containsKey(calculatedId)) {
                        result.get(calculatedId).add(createStubTranslationFromIndex(file, extensionKeyFromFile, String.valueOf(this.languageKey), tag, id));
                    } else {
                        result.put(calculatedId, new ArrayList<StubTranslation>() {{
                            createStubTranslationFromIndex(file, extensionKeyFromFile, String.valueOf(languageKey), tag, id);
                        }});
                    }
                }
            }
        }

        @NotNull
        @Override
        PsiElementPattern.Capture<PsiElement> getElementPattern() {

            return PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("label").withParent(
                PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("languageKey").withParent(
                    PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("data").withParent(
                        PlatformPatterns.psiElement(XmlElementType.XML_TAG).withName("T3locallang").withParent(
                            PlatformPatterns.psiElement(XmlElementType.XML_DOCUMENT)
                        )
                    )
                )
            );
        }
    }
}
