package com.cedricziel.idea.typo3.index.php;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class LegacyClassesForIDEIndex extends FileBasedIndexExtension<String, String> {

    public static ID<String, String> KEY = ID.create("com.cedricziel.idea.typo3.index.php.legacy_classes");

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    public static boolean isLegacyClass(@NotNull Project project, @NotNull String fqn) {

        return FileBasedIndex.getInstance().getAllKeys(LegacyClassesForIDEIndex.KEY, project).contains(fqn);
    }

    @Nullable
    public static String findReplacementClass(@NotNull Project project, @NotNull String fqn) {
        List<String> values = FileBasedIndex.getInstance().getValues(KEY, fqn, GlobalSearchScope.allScope(project));
        if (values.size() > 0) {
            return values.iterator().next();
        }

        return null;
    }

    @NotNull
    @Override
    public ID<String, String> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String, FileContent> getIndexer() {
        return inputData -> {
            Map<String, String> map = new THashMap<>();

            LegacyClassesRecursiveVisitor visitor = new LegacyClassesRecursiveVisitor();
            visitor.visitElement(inputData.getPsiFile());

            map.putAll(visitor.getMap());

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<String> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getName().equals("LegacyClassesForIde.php");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    private static class LegacyClassesRecursiveVisitor extends PsiRecursiveElementVisitor {
        private final Map<String, String> map;

        LegacyClassesRecursiveVisitor() {
            map = new THashMap<>();
        }

        public Map<String, String> getMap() {
            return map;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {

            if (!PlatformPatterns.psiElement(PhpClass.class).accepts(element)) {
                super.visitElement(element);
                return;
            }

            PhpClass phpClass = (PhpClass) element;

            String fqn = phpClass.getFQN();

            String superFqn = null;
            if (!phpClass.isInterface()) {
                superFqn = phpClass.getSuperFQN();
            } else {
                List<ClassReference> referenceElements = phpClass.getExtendsList().getReferenceElements();
                for (ClassReference cr : referenceElements) {
                    superFqn = cr.getFQN();
                }
            }

            if (superFqn != null) {
                map.put(fqn, superFqn);
            }

            super.visitElement(element);
        }
    }
}
