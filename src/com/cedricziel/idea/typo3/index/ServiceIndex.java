package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An index for service definitions made in `ext_localconf.php` files.
 */
public class ServiceIndex extends FileBasedIndexExtension<String, List<TYPO3ServiceDefinition>> {

    public static final ID<String, List<TYPO3ServiceDefinition>> KEY = ID.create("com.cedricziel.idea.typo3.domain.typo3_service_definition");

    private final static int VERSION = 1;

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    private final ObjectStreamDataExternalizer<List<TYPO3ServiceDefinition>> externalizer = new ObjectStreamDataExternalizer<>();

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() == PhpFileType.INSTANCE && file.getName().equals("ext_localconf.php");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID<String, List<TYPO3ServiceDefinition>> getName() {
        return KEY;
    }

    /**
     * Indexes the file content.
     */
    @NotNull
    @Override
    public DataIndexer<String, List<TYPO3ServiceDefinition>, FileContent> getIndexer() {

        return inputData -> {
            Map<String, List<TYPO3ServiceDefinition>> map = new THashMap<>();
            PsiFile psiFile = inputData.getPsiFile();
            psiFile.accept(new MyPsiRecursiveElementWalkingVisitor(map));

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<List<TYPO3ServiceDefinition>> getValueExternalizer() {
        return externalizer;
    }

    @Override
    public int getVersion() {
        return VERSION;
    }

    private class MyPsiRecursiveElementWalkingVisitor extends PsiRecursiveElementVisitor {

        private Map<String, List<TYPO3ServiceDefinition>> map;

        MyPsiRecursiveElementWalkingVisitor(Map<String, List<TYPO3ServiceDefinition>> map) {
            this.map = map;
        }

        @Override
        public void visitElement(PsiElement element) {
            if ((element instanceof MethodReference) && PhpElementsUtil.isMethodWithFirstStringOrFieldReference(element, "addService")) {
                visitServiceCreation(element);
            }
            super.visitElement(element);
        }

        private void visitServiceCreation(PsiElement element) {
            MethodReference methodReference = (MethodReference) element;
            // A service definition should contain at least 4 arguments
            if (methodReference.getParameters().length < 4) {
                return;
            }

            PsiElement extensionNameParam = methodReference.getParameters()[0]; // Extension name
            PsiElement serviceNameParam = methodReference.getParameters()[1]; // Service name
            PsiElement classNameParam = methodReference.getParameters()[2]; // Implementing Class (may be short name)
            PsiElement optionsArrayParam = methodReference.getParameters()[3]; // Array parameters

            String serviceId = serviceNameParam.getText();
            List<TYPO3ServiceDefinition> serviceMap;
            if (!map.containsKey(serviceId)) {
                serviceMap = new ArrayList<>();
                map.put(serviceId, serviceMap);
            } else {
                serviceMap = map.get(serviceId);
            }

            TYPO3ServiceDefinition serviceDefinition = new TYPO3ServiceDefinition(serviceId);
            serviceDefinition.setExtensionName(extensionNameParam.getText());
            if (classNameParam instanceof ClassConstantReference) {
                PhpExpression classReference = ((ClassConstantReference) classNameParam).getClassReference();
                if (classReference instanceof PhpReference) {
                    serviceDefinition.setClass(((PhpReference) classReference).getFQN());

                    PhpReference ref = (PhpReference) classReference;
                    serviceDefinition.setSignature(ref.getSignature());
                }

                if (optionsArrayParam instanceof ArrayCreationExpression) {
                    ArrayCreationExpression arrayExpression = (ArrayCreationExpression) optionsArrayParam;
                    mapOptionsArrayParam(serviceDefinition, arrayExpression.getHashElements());
                }

                serviceMap.add(serviceDefinition);
            }

            map.put(serviceId, serviceMap);
        }

        private void mapOptionsArrayParam(TYPO3ServiceDefinition serviceDefinition, Iterable<ArrayHashElement> optionsArray) {
            for (ArrayHashElement element : optionsArray) {
                if (null == element.getKey() || !(element.getKey() instanceof StringLiteralExpression)) {
                    continue;
                }

                String key = ((StringLiteralExpression) element.getKey()).getContents();
                // Assign string properties of the service definition options
                if (null != element.getValue() && element.getValue() instanceof StringLiteralExpression) {
                    String value = ((StringLiteralExpression) element.getValue()).getContents();
                    switch (key) {
                        case "os":
                            serviceDefinition.setOs(value);
                            break;
                        case "title":
                            serviceDefinition.setTitle(value);
                            break;
                        case "description":
                            serviceDefinition.setDescription(value);
                            break;
                        case "subtype":
                            serviceDefinition.setSubType(value);
                            break;
                        case "exec":
                            serviceDefinition.setExec(value);
                            break;
                    }
                }

                // Assign numbers
                if (null != element.getValue() && PhpPatterns.psiElement(PhpElementTypes.NUMBER).accepts(element.getValue())) {
                    switch (key) {
                        case "priority":
                            serviceDefinition.setPriority(new Integer(element.getValue().getText()));
                            break;
                        case "quality":
                            serviceDefinition.setQuality(new Integer(element.getValue().getText()));
                            break;
                    }
                }
            }
        }
    }
}
