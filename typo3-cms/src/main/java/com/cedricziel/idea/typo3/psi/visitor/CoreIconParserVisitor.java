package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.icons.IconStub;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class CoreIconParserVisitor extends PhpRecursiveElementVisitor {

    private Map<String, IconStub> map;

    public CoreIconParserVisitor() {
        this.map = new HashMap<>();
    }

    @NotNull
    public Map<String, IconStub> getMap () {
        return map;
    }

    @Override
    public void visitPhpField(Field field) {
        // TYPO3 7 through 8 use icons, 9 uses dynamic icons and the "staticIcons" field
        if (field.getName().equals("icons") || field.getName().equals("staticIcons")) {
            ArrayCreationExpression arrayCreation = PsiTreeUtil.findChildOfType(field, ArrayCreationExpression.class);
            if (arrayCreation != null) {
                visitIconDefinition(arrayCreation);
            }
        }

        super.visitPhpField(field);
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
