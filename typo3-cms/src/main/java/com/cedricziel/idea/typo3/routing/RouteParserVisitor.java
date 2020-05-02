package com.cedricziel.idea.typo3.routing;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Walks a psiFile recursively and parses the service definitions.
 */
public class RouteParserVisitor extends PsiRecursiveElementVisitor {

    private final String prefix;
    private Collection<RouteStub> routeStubs;

    public RouteParserVisitor() {
        this.prefix = "";
        this.routeStubs = new ArrayList<>();
    }

    public RouteParserVisitor(String prefix) {
        this.prefix = prefix;
        this.routeStubs = new ArrayList<>();
    }

    @NotNull
    public Collection<RouteStub> getRouteStubs() {
        return routeStubs;
    }

    @Override
    public void visitElement(@NotNull PsiElement element) {
        if (isRouteElement(element)) {
            ArrayHashElement arrayCreationExpression = (ArrayHashElement) element;
            visitRouteCreation(arrayCreationExpression);
        }
        super.visitElement(element);
    }

    private boolean isRouteElement(PsiElement element) {
        return PlatformPatterns.psiElement(ArrayHashElement.class).withParent(
                PlatformPatterns.psiElement(ArrayCreationExpression.class).withParent(
                        PlatformPatterns.psiElement(PhpReturn.class)
                )
        ).accepts(element);
    }

    private void visitRouteCreation(ArrayHashElement element) {

        RouteStub routeDefinition = new RouteStub();

        PhpPsiElement child = element.getKey();
        if (child instanceof StringLiteralExpression) {
            String key = ((StringLiteralExpression) child).getContents();

            PhpPsiElement valueMap = element.getValue();
            if (valueMap == null) {
                return;
            }

            routeDefinition.setTextRange(child.getTextRange());

            if (valueMap instanceof ArrayCreationExpression) {
                ArrayCreationExpression propertyArray = (ArrayCreationExpression) valueMap;

                routeDefinition.setName(prefix + key);

                for (ArrayHashElement routePropertyHashElement : propertyArray.getHashElements()) {
                    visitProperty(routeDefinition, routePropertyHashElement);
                }

                routeStubs.add(routeDefinition);
            }
        }
    }

    private void visitProperty(RouteStub routeDefinition, ArrayHashElement routePropertyHashElement) {
        if (!(routePropertyHashElement.getKey() instanceof StringLiteralExpression)) {
            return;
        }

        String propertyName = ((StringLiteralExpression) routePropertyHashElement.getKey()).getContents();
        PhpPsiElement arrayValue = routePropertyHashElement.getValue();

        if (arrayValue instanceof StringLiteralExpression) {
            StringLiteralExpression propertyValue = (StringLiteralExpression) arrayValue;
            if ("path".equals(propertyName)) {
                routeDefinition.setPath(propertyValue.getContents());
            }
            if ("access".equals(propertyName)) {
                routeDefinition.setAccess(propertyValue.getContents());
            }
        }

        if ("target".equals(propertyName)) {
            if (arrayValue != null) {
                String text = arrayValue.getText();
                text = text.replace("'", "").replace(".", "");

                String[] split = text.split("::");
                if (split.length == 3) {
                    routeDefinition.setController(split[0]);
                    routeDefinition.setMethod(split[2]);
                }

                if (arrayValue.getFirstChild() instanceof ClassConstantReference) {
                    ClassConstantReference classConstantReference = (ClassConstantReference) arrayValue.getFirstChild();
                    PhpExpression classReference = classConstantReference.getClassReference();
                    if (classReference instanceof ClassReference) {
                        routeDefinition.setController(((ClassReference) classReference).getFQN());
                    }
                }
            }
        }
    }
}
