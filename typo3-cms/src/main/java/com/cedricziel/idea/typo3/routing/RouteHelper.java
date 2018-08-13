package com.cedricziel.idea.typo3.routing;

import com.cedricziel.idea.typo3.index.RouteIndex;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RouteHelper {
    @NotNull
    public static Collection<RouteStub> routesFromRoutesPhp(@NotNull PsiFile psiFile) {
        RouteParserVisitor visitor;
        if (psiFile.getName().equals(RouteIndex.AJAX_ROUTES_PHP)) {
            visitor = new RouteParserVisitor("ajax_");
        } else {
            visitor = new RouteParserVisitor();
        }

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

        // Route definition
        Set<String> keys = new HashSet<>();
        keys.add(routeName);
        FileBasedIndex.getInstance().getFilesWithKey(RouteIndex.KEY, keys, virtualFile -> {
            FileBasedIndex.getInstance().processValues(RouteIndex.KEY, routeName, virtualFile, (file, value) -> {
                PsiFile file1 = PsiManager.getInstance(project).findFile(file);
                if (file1 != null) {
                    PsiElement elementAt = file1.findElementAt(value.getTextRange().getStartOffset());
                    if (elementAt != null) {
                        results.add(elementAt.getParent());
                    }
                }

                return true;
            }, GlobalSearchScope.allScope(project));

            return true;
        }, GlobalSearchScope.allScope(project));

        return results.toArray(new PsiElement[0]);
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

        return result.toArray(new PsiElement[0]);
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
