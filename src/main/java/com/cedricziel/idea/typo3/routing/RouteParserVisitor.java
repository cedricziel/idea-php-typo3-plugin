package com.cedricziel.idea.typo3.routing;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class RouteParserVisitor extends PsiRecursiveElementVisitor {

    private Collection<RouteStub> routeStubs;

    public RouteParserVisitor() {
        routeStubs = new ArrayList<>();
    }

    @NotNull
    public Collection<RouteStub> getRouteStubs() {
        return routeStubs;
    }

    @Override
    public void visitElement(PsiElement element) {
        if ((element instanceof ArrayCreationExpression)) {
            ArrayCreationExpression arrayCreationExpression = (ArrayCreationExpression) element;
            visitRouteCreation(arrayCreationExpression);
        }
        super.visitElement(element);
    }

    private void visitRouteCreation(ArrayCreationExpression element) {

        RouteStub routeDefinition = new RouteStub();

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
                        if ("target".equals(propertyName)) {
                            PhpPsiElement value = routePropertyHashElement.getValue();
                            String text = value.getText();
                            text = text.replace("'", "").replace(".", "");

                            String[] split = text.split("::");
                            if (split.length == 3) {
                                routeDefinition.setController(split[0]);
                                routeDefinition.setMethod(split[2]);
                            }
                        }
                    }

                    routeStubs.add(routeDefinition);
                }
            }
        }
    }
}
