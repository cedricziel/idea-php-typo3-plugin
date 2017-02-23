package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class CoreIconParserVisitor extends PsiRecursiveElementVisitor {

    private Map<String, List<TYPO3IconDefinition>> map;

    public CoreIconParserVisitor(Map<String, List<TYPO3IconDefinition>> map) {
        this.map = map;
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
                TYPO3IconDefinition iconDefinition = new TYPO3IconDefinition();
                String key = ((StringLiteralExpression) child).getContents();
                iconDefinition.setName(key);
                iconDefinition.setElement(child);

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

                            iconDefinition.setProvider(((ClassConstantReference) iconPropertyHashElement.getValue()));
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

                    if (map.containsKey(key)) {
                        map.get(key).add(iconDefinition);
                        continue;
                    }
                    List<TYPO3IconDefinition> definitionList = new ArrayList<>();
                    definitionList.add(iconDefinition);
                    map.put(key, definitionList);
                }
            }
        }
    }
}
