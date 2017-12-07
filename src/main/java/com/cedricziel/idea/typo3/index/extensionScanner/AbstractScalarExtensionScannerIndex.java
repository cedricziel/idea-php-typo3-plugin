package com.cedricziel.idea.typo3.index.extensionScanner;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractScalarExtensionScannerIndex extends FileBasedIndexExtension<String, Integer> {

    protected final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    protected final ObjectStreamDataExternalizer<Integer> myExternalizer = new ObjectStreamDataExternalizer<>();

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Integer> getValueExternalizer() {
        return myExternalizer;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
