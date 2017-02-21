package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.cedricziel.idea.typo3.index.externalizer.StringSetDataExternalizer;
import com.cedricziel.idea.typo3.psi.visitor.CoreServiceDefinitionParserVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CoreServiceMapStubIndex extends FileBasedIndexExtension<String, Set<String>> {

    public static final ID<String, Set<String>> KEY = ID.create("com.cedricziel.idea.typo3.index.core_service_map");
    public static final String EXT_LOCALCONF_PHP_FILENAME = "ext_localconf.php";

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public DataIndexer<String, Set<String>, FileContent> getIndexer() {

        return inputData -> {

            final Map<String, Set<String>> map = new THashMap<>();

            PsiFile psiFile = inputData.getPsiFile();

            if (psiFile instanceof PhpFile) {
                Map<String, ArrayList<TYPO3ServiceDefinition>> serviceMap = new THashMap<>();
                psiFile.accept(new CoreServiceDefinitionParserVisitor(serviceMap));

                serviceMap.forEach((serviceId, definitionList) -> {
                    Set<String> implementations = new HashSet<>();
                    definitionList.forEach(typo3ServiceDefinition -> implementations.add(typo3ServiceDefinition.getClassName()));
                    map.put(serviceId, implementations);
                });
            }

            return map;
        };
    }

    @NotNull
    @Override
    public ID<String, Set<String>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    public DataExternalizer<Set<String>> getValueExternalizer() {
        return new StringSetDataExternalizer();
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() == PhpFileType.INSTANCE && file.getName().equals(EXT_LOCALCONF_PHP_FILENAME);
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
