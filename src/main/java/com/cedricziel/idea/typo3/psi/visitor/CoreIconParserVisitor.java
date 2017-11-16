package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.IconStub;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class CoreIconParserVisitor extends PsiRecursiveElementVisitor {

    private Map<String, IconStub> map;

    public CoreIconParserVisitor() {
        this.map = new HashMap<>();
    }

    @NotNull
    public Map<String, IconStub> getMap () {
        return map;
    }

    @Override
    public void visitElement(PsiElement element) {
        if ((element instanceof ArrayCreationExpression)) {
            visitIconDefinition((ArrayCreationExpression) element);
        }
        super.visitElement(element);
    }

    private void visitIconDefinition(ArrayCreationExpression element) {

        for (ArrayHashElement hashElement : element.getHashElements()) {
            PhpPsiElement child = hashElement.getKey();
            if (child instanceof StringLiteralExpression) {
                String key = ((StringLiteralExpression) child).getContents();
                IconStub iconDefinition = new IconStub(key, hashElement);

                PhpPsiElement valueMap = hashElement.getValue();
                if (valueMap == null) {
                    continue;
                }

                if (valueMap instanceof ArrayCreationExpression) {
                    ArrayCreationExpression propertyArray = (ArrayCreationExpression) valueMap;

                    for (ArrayHashElement iconPropertyHashElement : propertyArray.getHashElements()) {
                        String propertyName = ((StringLiteralExpression) iconPropertyHashElement.getKey()).getContents();
                        if ("provider".equals(propertyName)) {
                            if(!(iconPropertyHashElement.getValue() instanceof ClassConstantReference)) {
                                continue;
                            }

                            iconDefinition.setProvider(((ClassConstantReference) iconPropertyHashElement.getValue()).getFQN());
                        }
                        if ("options".equals(propertyName)) {
                            if (!(iconPropertyHashElement.getValue() instanceof ArrayCreationExpression)) {
                                continue;
                            }
                            for (ArrayHashElement iconPropertyOptionsHashElement : ((ArrayCreationExpression) iconPropertyHashElement.getValue()).getHashElements()) {
                                String optionName = ((StringLiteralExpression) iconPropertyOptionsHashElement.getKey()).getContents();
                                if ("source".equals(optionName)) {
                                    iconDefinition.setSource(((StringLiteralExpression) iconPropertyOptionsHashElement.getValue()).getContents());
                                }
                            }
                        }
                    }

                    map.put(key, iconDefinition);
                }
            }
        }
    }
}
