package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.extbase.model.ExtbaseController;
import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ExtbaseControllerStubIndex extends FileBasedIndexExtension<String, ExtbaseController> {

    public static final ID<String, ExtbaseController> KEY = ID.create("com.cedricziel.idea.typo3.index.extbase_controller");
    private static ObjectStreamDataExternalizer<ExtbaseController> EXTERNALIZER = new ObjectStreamDataExternalizer<>();
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return virtualFile -> {
            FileType fileType = virtualFile.getFileType();

            return fileType == PhpFileType.INSTANCE
                    && virtualFile.getName().contains("Controller.php");
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer getIndexer() {
        return new ExtbaseControllerIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer getValueExternalizer() {
        return EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    private static class ExtbaseControllerIndexer implements DataIndexer<String, ExtbaseController, FileContent> {
        @NotNull
        @Override
        public Map<String, ExtbaseController> map(@NotNull FileContent fileContent) {
            THashMap<String, ExtbaseController> map = new THashMap<>();

            PsiFile psiFile = fileContent.getPsiFile();
            return map;
        }
    }
}
