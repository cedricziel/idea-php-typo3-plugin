package com.cedricziel.idea.typo3.routing;

import com.cedricziel.idea.typo3.index.RouteIndex;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteHelper {
    @NotNull
    public static Collection<RouteStub> routesFromRoutesPhp(@NotNull PsiFile psiFile) {

        RouteParserVisitor visitor = new RouteParserVisitor();
        visitor.visitElement(psiFile);

        return visitor.getRouteStubs();
    }

    @NotNull
    public static PsiElement[] getRouteDefinitionElements(@NotNull Project project, @NotNull String routeName) {
        List<PsiElement> results = new ArrayList<>();

        for (PsiElement psiElement : RouteHelper.getTargetMethods(project, routeName)) {

            if (psiElement instanceof Method) {
                results.add(psiElement);
            }

        }

        return results.toArray(new PsiElement[results.size()]);
    }

    @NotNull
    private static PsiElement[] getTargetMethods(@NotNull Project project, @NotNull String routeName) {
        List<PsiElement> result = new ArrayList<>();
        List<RouteStub> values = FileBasedIndex.getInstance().getValues(RouteIndex.KEY, routeName, GlobalSearchScope.allScope(project));
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        for (RouteStub routeStub : values) {
            String fqn = routeStub.getController();

            Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN(fqn);
            classesByFQN.forEach(c -> {
                if (c.findMethodByName(routeStub.getMethod()) != null) {
                    result.add(c.findMethodByName(routeStub.getMethod()));
                }
            });
        }

        return result.toArray(new PsiElement[result.size()]);
    }

    @NotNull
    public static Collection<LookupElement> getRoutesLookupElements(@NotNull Project project) {
        Collection<LookupElement> routeLookupElements = new ArrayList<>();

        Collection<String> routes = FileBasedIndex.getInstance().getAllKeys(RouteIndex.KEY, project);
        for (String routeName : routes) {
            List<RouteStub> values = FileBasedIndex.getInstance().getValues(RouteIndex.KEY, routeName, GlobalSearchScope.allScope(project));
            values.forEach(r -> routeLookupElements.add(new RouteLookupElement(r)));
        }

        return routeLookupElements;
    }
}
