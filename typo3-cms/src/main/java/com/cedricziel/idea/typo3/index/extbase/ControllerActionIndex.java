package com.cedricziel.idea.typo3.index.extbase;

import com.cedricziel.idea.typo3.extbase.controller.StubControllerAction;
import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.cedricziel.idea.typo3.util.ExtensionUtility;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ControllerActionIndex extends FileBasedIndexExtension<String, StubControllerAction> {

    public static final ID<String, StubControllerAction> KEY = ID.create("com.cedricziel.idea.typo3.extbase.controller_action");

    @NotNull
    @Override
    public ID<String, StubControllerAction> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, StubControllerAction, FileContent> getIndexer() {
        return inputData -> {
            Map<String, StubControllerAction> map = new THashMap<>();

            String extensionKeyFromFile = ExtensionUtility.findExtensionKeyFromFile(inputData.getFile());
            if (null != extensionKeyFromFile) {
                ControllerActionRecursiveVisitor visitor = new ControllerActionRecursiveVisitor();
                visitor.visitFile(inputData.getPsiFile());
                Map<String, StubControllerAction> stringStubControllerActionMap = visitor.getMap();
                stringStubControllerActionMap.forEach((x, y) -> y.setExtensionName(extensionKeyFromFile));

                map.putAll(stringStubControllerActionMap);
            }

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return new EnumeratorStringDescriptor();
    }

    @NotNull
    @Override
    public DataExternalizer<StubControllerAction> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() == PhpFileType.INSTANCE && file.getNameWithoutExtension().contains("Controller");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    static class ControllerActionRecursiveVisitor extends PsiRecursiveElementVisitor {
        final Map<String, StubControllerAction> map;

        ControllerActionRecursiveVisitor() {
            this.map = new THashMap<>();
        }

        public Map<String, StubControllerAction> getMap() {
            return map;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {
            if (element instanceof Method) {
                Method m = (Method) element;
                if (!m.getName().endsWith("Action") || !m.getModifier().isPublic()) {
                    super.visitElement(element);

                    return;
                }
                StubControllerAction stubControllerAction = new StubControllerAction();

                PhpClass containingClass = m.getContainingClass();
                if (containingClass == null) {
                    super.visitElement(element);

                    return;
                }

                if (!ExtbaseUtility.isActionController(containingClass)) {
                    super.visitElement(element);

                    return;
                }

                stubControllerAction.setName(m.getName());
                stubControllerAction.setControllerName(containingClass.getName());
                stubControllerAction.setControllerFQN(containingClass.getFQN());
                stubControllerAction.setTextRange(m.getTextRange());


                map.put(m.getName().replace("Action", ""), stubControllerAction);
            }

            super.visitElement(element);
        }
    }
}
