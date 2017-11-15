package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablenameFileIndex extends FileBasedIndexExtension <String, TextRange> {

    public static ID<String, TextRange> KEY = ID.create("com.cedricziel.idea.typo3.index.tablename");

    @NotNull
    @Override
    public ID<String, TextRange> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, TextRange, FileContent> getIndexer() {
        return inputData -> {
            Map<String, TextRange> map = new THashMap<>();

            CharSequence charSequence = LoadTextUtil.loadText(inputData.getFile());

            final Matcher matcher = Pattern
                    .compile("create\\s+table\\s+(if\\s+not\\s+exists\\s+)?([a-zA-Z_0-9]+)", Pattern.CASE_INSENSITIVE)
                    .matcher(charSequence);

            try {
                while (matcher.find()) {
                    if (matcher.groupCount() < 2) {
                        return map;
                    }

                    map.put(matcher.group(2), new TextRange(matcher.start(), matcher.end()));
                }
            } catch (IllegalStateException e) {
                // no matches
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
    public DataExternalizer<TextRange> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getName().equals("ext_tables.sql");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
