package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.container.RouteProvider;
import com.cedricziel.idea.typo3.domain.TYPO3RouteDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
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

    private final String type;
    private Map<String, List<TYPO3RouteDefinition>> routes;
    private Map<String, List<TYPO3RouteDefinition>> ajaxRoutes;

    public RouteDefinitionParserVisitor(Map<String, List<TYPO3RouteDefinition>> routes, Map<String, List<TYPO3RouteDefinition>> ajaxRoutes, String type) {
        this.routes = routes;
        this.ajaxRoutes = ajaxRoutes;
        this.type = type;
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

        Map<String, List<TYPO3RouteDefinition>> routeSet;
        if (type.equals(RouteProvider.ROUTE_TYPE_BACKEND)) {
            routeSet = this.routes;
        } else {
            routeSet = this.ajaxRoutes;
        }

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
                    routeDefinition.setType(type);

                    for (ArrayHashElement routePropertyHashElement : propertyArray.getHashElements()) {
                        String propertyName = ((StringLiteralExpression) routePropertyHashElement.getKey()).getContents();
                        if ("path".equals(propertyName)) {
                            routeDefinition.setPath(((StringLiteralExpression) routePropertyHashElement.getValue()).getContents());
                        }
                        if ("access".equals(propertyName)) {
                            routeDefinition.setPath(((StringLiteralExpression) routePropertyHashElement.getValue()).getContents());
                        }
                    }

                    if (!routeSet.containsKey(key)) {
                        routeSet.put(key, new ArrayList<>());
                    }

                    routeSet.get(key).add(routeDefinition);
                }
            }
        }
    }
}
