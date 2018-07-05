package com.cedricziel.idea.typo3.index.extensionScanner;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MethodArgumentDroppedIndex extends AbstractScalarExtensionScannerIndex {

    public static ID<String, Integer> KEY = ID.create("com.cedricziel.idea.typo3.extension_scanner.method_argument_dropped");

    @NotNull
    @Override
    public ID<String, Integer> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return x -> x.getName().equals("MethodArgumentDroppedMatcher.php");
    }

    @NotNull
    @Override
    public DataIndexer<String, Integer, FileContent> getIndexer() {
        return inputData -> {
            Set<PsiElement> elements = new HashSet<>();

            Collections.addAll(
                    elements,
                    PsiTreeUtil.collectElements(inputData.getPsiFile(), el -> PlatformPatterns
                            .psiElement(StringLiteralExpression.class)
                            .withParent(
                                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                                            .withAncestor(
                                                    4,
                                                    PlatformPatterns.psiElement(PhpElementTypes.RETURN)
                                            )
                            )
                            .accepts(el)
                    )
            );

            Map<String, Integer> methodMap = new HashMap<>();

            for (PsiElement gatheredElement : elements) {
                PsiElement parent = gatheredElement.getParent().getParent();
                if (parent instanceof ArrayHashElement) {
                    ArrayHashElement arr = (ArrayHashElement) parent;
                    PhpPsiElement value = arr.getValue();
                    if (value != null && value instanceof ArrayCreationExpression) {
                        ArrayCreationExpression creationExpression = (ArrayCreationExpression) value;
                        creationExpression.getHashElements().forEach(x -> {
                            PhpPsiElement key = x.getKey();
                            if (key != null && key.getText().contains("maximumNumberOfArguments")) {
                                methodMap.put("\\" + ((StringLiteralExpression) gatheredElement).getContents(), Integer.parseInt(x.getValue().getText()));
                            }
                        });
                    }
                }
            }

            return methodMap;
        };
    }
}
