package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class CoreServiceDefinitionParserVisitor extends PsiRecursiveElementVisitor {

    private final Map<String, ArrayList<TYPO3ServiceDefinition>> map;

    public CoreServiceDefinitionParserVisitor(Map<String, ArrayList<TYPO3ServiceDefinition>> map) {
        this.map = map;
    }

    @Override
    public void visitElement(@NotNull PsiElement element) {
        if (element instanceof MethodReference) {
            visitServiceCreation(element);
        }
        super.visitElement(element);
    }

    private void visitServiceCreation(PsiElement element) {
        MethodReference methodReference = (MethodReference) element;
        // A service definition should contain at least 4 arguments
        if (methodReference.getName() == null || !methodReference.getName().equals("addService") || methodReference.getParameters().length < 4) {
            return;
        }

        PsiElement extensionNameParam = methodReference.getParameters()[0]; // Extension name
        PsiElement serviceNameParam = methodReference.getParameters()[1]; // Service name
        // PsiElement serviceKeyParam = methodReference.getParameters()[2]; // Implementing Class (may be short name)
        PsiElement optionsArrayParam = methodReference.getParameters()[3]; // Array parameters

        String serviceId;
        if (serviceNameParam instanceof StringLiteralExpression) {
            StringLiteralExpression name = (StringLiteralExpression) serviceNameParam;
            serviceId = name.getContents();
        } else {
            serviceId = serviceNameParam.getText();
        }
        ArrayList<TYPO3ServiceDefinition> serviceMap;
        if (!map.containsKey(serviceId)) {
            serviceMap = new ArrayList<>();
            map.put(serviceId, serviceMap);
        }

        serviceMap = map.get(serviceId);

        TYPO3ServiceDefinition serviceDefinition = new TYPO3ServiceDefinition(serviceId);
        serviceDefinition.setExtensionName(extensionNameParam.getText());

        if (optionsArrayParam instanceof ArrayCreationExpression) {
            ArrayCreationExpression arrayExpression = (ArrayCreationExpression) optionsArrayParam;
            mapOptionsArrayParam(serviceDefinition, arrayExpression.getHashElements());
        }

        serviceMap.add(serviceDefinition);

        map.put(serviceId, serviceMap);
    }

    private void mapOptionsArrayParam(TYPO3ServiceDefinition serviceDefinition, Iterable<ArrayHashElement> optionsArray) {
        for (ArrayHashElement element : optionsArray) {
            if (null == element.getKey() || !(element.getKey() instanceof StringLiteralExpression)) {
                continue;
            }

            String key = ((StringLiteralExpression) element.getKey()).getContents();
            if (null != element.getValue() && element.getValue() instanceof ClassConstantReference) {
                ClassConstantReference value = (ClassConstantReference) element.getValue();
                switch (key) {
                    case "className":
                        PhpExpression classReference = value.getClassReference();
                        if (classReference instanceof PhpReference) {
                            PhpReference ref = (PhpReference) classReference;
                            serviceDefinition.setClass(ref.getFQN());
                            serviceDefinition.setClassName(ref.getFQN());
                            serviceDefinition.setSignature(ref.getSignature());
                        }

                        break;
                }
            }

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
                    case "className":
                        serviceDefinition.setClassName(value);
                        serviceDefinition.setClass(value);
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

    public Map<? extends String, ? extends ArrayList<TYPO3ServiceDefinition>> getServiceMap() {
        return map;
    }
}
