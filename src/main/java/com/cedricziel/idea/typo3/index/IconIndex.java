package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.domain.IconStub;
import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.psi.visitor.CoreFlagParserVisitor;
import com.cedricziel.idea.typo3.psi.visitor.CoreIconParserVisitor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.cedricziel.idea.typo3.util.IconUtil.ICON_REGISTRY_CLASS;

public class IconIndex extends FileBasedIndexExtension<String, IconStub> {

    public static ID<String, IconStub> KEY = ID.create("com.cedricziel.idea.typo3.index.icon");

    public static Collection<String> getAllAvailableIcons(@NotNull Project project) {

        return FileBasedIndex.getInstance().getAllKeys(KEY, project);
    }

    public static Map<VirtualFile, IconStub> getIconDefinitionByIdentifier(@NotNull Project project, String iconIdentifier) {
        Set<String> identifiers = new HashSet<>();
        identifiers.add(iconIdentifier);

        Map<VirtualFile, IconStub> icons = new THashMap<>();

        FileBasedIndex.getInstance().getFilesWithKey(KEY, identifiers, virtualFile -> {
            FileBasedIndex.getInstance().processValues(KEY, iconIdentifier, virtualFile, (file, value) -> {
                icons.put(file, value);

                return true;
            }, GlobalSearchScope.allScope(project));

            return true;
        }, GlobalSearchScope.allScope(project));

        return icons;
    }

    @NotNull
    public static PsiElement[] getIconDefinitionElements(@NotNull Project project, @NotNull String identifier) {
        Map<VirtualFile, IconStub> iconDefinitionByIdentifier = getIconDefinitionByIdentifier(project, identifier);
        if (iconDefinitionByIdentifier.size() > 0) {
            return iconDefinitionByIdentifier
                    .keySet()
                    .stream()
                    .map(virtualFile -> {
                        IconStub iconStub = iconDefinitionByIdentifier.get(virtualFile);
                        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                        return file != null ? file.findElementAt(iconStub.getTextRange().getStartOffset()) : null;
                    })
                    .filter(Objects::nonNull)
                    .toArray(PsiElement[]::new);
        }

        return new PsiElement[0];
    }

    @NotNull
    @Override
    public ID<String, IconStub> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, IconStub, FileContent> getIndexer() {
        return inputData -> {
            Map<String, IconStub> map = new THashMap<>();

            // index the icon registry
            if (inputData.getPsiFile() instanceof PhpFile) {
                PhpClass iconRegistry = PhpPsiUtil.findClass((PhpFile) inputData.getPsiFile(), phpClass -> {
                    String presentableFQN = phpClass.getPresentableFQN();
                    return presentableFQN.equals(ICON_REGISTRY_CLASS);
                });

                if (iconRegistry != null) {
                    for (PsiElement element : iconRegistry.getChildren()) {
                        if (PlatformPatterns.psiElement(PhpElementTypes.CLASS_FIELDS).accepts(element)) {
                            for (PsiElement fieldsInner : element.getChildren()) {
                                if (PlatformPatterns.psiElement(PhpElementTypes.CLASS_FIELD).accepts(fieldsInner)) {

                                    Field field = (Field) fieldsInner;

                                    // TYPO3 7 through 8 use icons, 9 uses dynamic icons and the "staticIcons" field
                                    if (field.getName().equals("icons") || field.getName().equals("staticIcons")) {
                                        CoreIconParserVisitor visitor = new CoreIconParserVisitor();
                                        visitor.visitElement(field.getDefaultValue());

                                        visitor.getMap().forEach(map::put);
                                    }
                                }
                            }
                        }

                        if (PlatformPatterns.psiElement(PhpElementTypes.CLASS_METHOD).accepts(element)) {
                            Method method = (Method) element;
                            if ("registerFlags".equals(method.getName())) {
                                CoreFlagParserVisitor visitor = new CoreFlagParserVisitor();
                                method.accept(visitor);
                                visitor.visitElement(method);

                                visitor.getMap().forEach(map::put);
                            }
                        }
                    }
                }
            }

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataExternalizer<IconStub> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            String extension = file.getExtension();

            return extension != null && extension.equalsIgnoreCase("php");
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
