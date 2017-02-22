package com.cedricziel.idea.typo3.container;

import com.cedricziel.idea.typo3.domain.TYPO3RouteDefinition;
import com.cedricziel.idea.typo3.psi.visitor.RouteDefinitionParserVisitor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.PhpFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Traverses the known places for route definitions and collects them.
 */
public class RouteProvider {
    public static final String ROUTES_DEFINITION_FILENAME = "Routes.php";
    public static final String AJAXROUTES_DEFINITION_FILENAME = "AjaxRoutes.php";
    public static final String ROUTE_TYPE_BACKEND = "web";
    public static final String ROUTE_TYPE_AJAX = "ajax";

    private HashMap<String, List<TYPO3RouteDefinition>> routes;
    private HashMap<String, List<TYPO3RouteDefinition>> ajaxRoutes;

    public RouteProvider() {
        routes = new HashMap<>();
        ajaxRoutes = new HashMap<>();
    }

    private void collectRoutes(Project project) {
        FileBasedIndex index = FileBasedIndex.getInstance();
        Collection<VirtualFile> containingFiles = index.getContainingFiles(
                FileTypeIndex.NAME,
                PhpFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );

        containingFiles.removeIf(virtualFile -> !(virtualFile.getName().contains(ROUTES_DEFINITION_FILENAME) || virtualFile.getName().contains(AJAXROUTES_DEFINITION_FILENAME)));

        for (VirtualFile projectFile : containingFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(projectFile);
            if (psiFile != null) {
                String type = projectFile.getName().contains(AJAXROUTES_DEFINITION_FILENAME) ? ROUTE_TYPE_AJAX : ROUTE_TYPE_BACKEND;
                psiFile.accept(new RouteDefinitionParserVisitor(routes, ajaxRoutes, type));
            }
        }
    }

    public void collect(Project project) {
        collectRoutes(project);
    }

    public Boolean has(Project project, String routeName) {
        return null;
    }

    public List<TYPO3RouteDefinition> resolve(String routeName) {
        return null;
    }

    public List<TYPO3RouteDefinition> all() {
        List<TYPO3RouteDefinition> list = new ArrayList<>();
        ajaxRoutes.values().forEach(ajaxRouteDefs -> ajaxRouteDefs.forEach(list::add));
        routes.values().forEach(routeDefs -> routeDefs.forEach(list::add));

        return list;
    }

    public List<TYPO3RouteDefinition> ajax() {
        List<TYPO3RouteDefinition> list = new ArrayList<>();
        ajaxRoutes.values().forEach(ajaxRouteDefs -> ajaxRouteDefs.forEach(list::add));

        return list;
    }
}
