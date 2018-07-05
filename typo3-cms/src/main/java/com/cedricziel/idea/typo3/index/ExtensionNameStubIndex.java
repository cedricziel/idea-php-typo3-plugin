package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ExtensionNameStubIndex extends FileBasedIndexExtension<String, String> {

    private static ObjectStreamDataExternalizer<String> EXTERNALIZER = new ObjectStreamDataExternalizer<>();

    public static final ID<String, String> KEY = ID.create("com.cedricziel.idea.typo3.index.extension_name");

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, String> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String, FileContent> getIndexer() {
        return virtualFile -> {
            final Map<String, String> items = new THashMap<>();

            VirtualFile file = virtualFile.getFile();
            VirtualFile parentDirectory = file.getParent();
            if (parentDirectory != null) {
                VirtualFile ancestorDirectory = parentDirectory.getParent();
                if (ancestorDirectory != null && ancestorDirectory.isDirectory()) {
                    String ancestorDirectoryName = ancestorDirectory.getName();
                    if (ancestorDirectoryName.equals("sysext") || ancestorDirectoryName.equals("ext")) {
                        String path = file.getPath();
                        items.put(parentDirectory.getName(), path);
                    }

                    // handle the case where the extension name can *not* be inferred from the directory name
                }
            }

            return items;
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
        return EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return virtualFile -> virtualFile.getName().equals("ext_emconf.php");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
