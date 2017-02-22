package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.TYPO3RouteDefinition;
import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class RouteDefinitionParserVisitor extends PsiRecursiveElementVisitor {

    private Map<String, List<TYPO3RouteDefinition>> routes;
    private Map<String, List<TYPO3RouteDefinition>> ajaxRoutes;

    public RouteDefinitionParserVisitor(Map<String, List<TYPO3RouteDefinition>> routes, Map<String, List<TYPO3RouteDefinition>> ajaxRoutes) {
        this.routes = routes;
        this.ajaxRoutes = ajaxRoutes;
    }

    @Override
    public void visitElement(PsiElement element) {
        if ((element instanceof ArrayCreationExpression)) {
            ArrayCreationExpression arrayCreationExpression = (ArrayCreationExpression) element;
            visitRouteCreation(arrayCreationExpression);
        }
        super.visitElement(element);
    }

    /**
     * The following element structure should be parsed:
     * <pre>
     * return [
     * // Login screen of the TYPO3 Backend
     * 'login' => [
     * 'path' => '/login',
     * 'access' => 'public',
     * 'target' => Controller\LoginController::class . '::formAction'
     * ],
     * ]
     * </pre>
     *
     * @param element
     */
    private void visitRouteCreation(ArrayCreationExpression element) {

        TYPO3RouteDefinition routeDefinition = new TYPO3RouteDefinition();

        for (ArrayHashElement arrayHashElement : element.getHashElements()) {
            PhpPsiElement child = arrayHashElement.getKey();
            if (child instanceof StringLiteralExpression) {
                String key = ((StringLiteralExpression) child).getContents();

                PhpPsiElement valueMap = arrayHashElement.getValue();
                if (valueMap == null) {
                    continue;
                }

                if (valueMap instanceof ArrayCreationExpression) {
                    ArrayCreationExpression propertyArray = (ArrayCreationExpression) valueMap;

                    routeDefinition.setName(key);

                    for (ArrayHashElement routePropertyHashElement : propertyArray.getHashElements()) {
                        String propertyName = ((StringLiteralExpression) routePropertyHashElement.getKey()).getContents();
                        if ("path".equals(propertyName)) {
                            routeDefinition.setPath(((StringLiteralExpression) routePropertyHashElement.getValue()).getContents());
                        }
                        if ("access".equals(propertyName)) {
                            routeDefinition.setPath(((StringLiteralExpression) routePropertyHashElement.getValue()).getContents());
                        }
                    }

                    if (!routes.containsKey(key)) {
                        routes.put(key, new ArrayList<>());
                    }

                    routes.get(key).add(routeDefinition);
                }
            }
        }
        /*
        MethodReference methodReference = (MethodReference) element;
        // A service definition should contain at least 4 arguments
        if (methodReference.getParameters().length < 4) {
            return;
        }

        PsiElement extensionNameParam = methodReference.getParameters()[0]; // Extension name
        PsiElement serviceNameParam = methodReference.getParameters()[1]; // Service name
        PsiElement classNameParam = methodReference.getParameters()[2]; // Implementing Class (may be short name)
        PsiElement optionsArrayParam = methodReference.getParameters()[3]; // Array parameters

        String serviceId;
        if (serviceNameParam instanceof StringLiteralExpression) {
            StringLiteralExpression name = (StringLiteralExpression) serviceNameParam;
            serviceId = name.getContents();
        } else {
            serviceId = serviceNameParam.getText();
        }
        ArrayList<TYPO3ServiceDefinition> serviceMap;
        if (!routes.containsKey(serviceId)) {
            serviceMap = new ArrayList<>();
            routes.put(serviceId, serviceMap);
        }

        serviceMap = routes.get(serviceId);

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

        routes.put(serviceId, serviceMap);
        */
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
