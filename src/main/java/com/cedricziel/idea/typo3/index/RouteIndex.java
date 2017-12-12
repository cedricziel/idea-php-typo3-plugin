package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.routing.RouteHelper;
import com.cedricziel.idea.typo3.routing.RouteStub;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.elements.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RouteIndex extends FileBasedIndexExtension<String, RouteStub> {

    public static final ID<String, RouteStub> KEY = ID.create("com.cedricziel.idea.typo3.index.route");

    @NotNull
    public static boolean hasRoute(@NotNull Project project, @NotNull String routeName) {
        return FileBasedIndex.getInstance().getAllKeys(KEY, project).contains(routeName);
    }

    @NotNull
    public static Collection<RouteStub> getRoute(@NotNull Project project, @NotNull String value) {
        return FileBasedIndex.getInstance().getValues(KEY, value, GlobalSearchScope.allScope(project));
    }

    @NotNull
    @Override
    public ID<String, RouteStub> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, RouteStub, FileContent> getIndexer() {
        return inputData -> {
            Map<String, RouteStub> map = new THashMap<>();

            if (inputData.getFile().getName().equals("ext_tables.php")) {
                ExtTablesRouteVisitor extTablesRouteVisitor = new ExtTablesRouteVisitor();
                extTablesRouteVisitor.visitFile(inputData.getPsiFile());
                for (RouteStub routeStub : extTablesRouteVisitor.getRouteStubs()) {
                    if (routeStub != null && routeStub.getName() != null) {
                        map.put(routeStub.getName(), routeStub);
                    }
                }
            } else {
                for (RouteStub route : RouteHelper.routesFromRoutesPhp(inputData.getPsiFile())) {
                    map.put(route.getName(), route);
                }
            }

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return new EnumeratorStringDescriptor();
    }

    @NotNull
    @Override
    public DataExternalizer<RouteStub> getValueExternalizer() {
        return new ObjectStreamDataExternalizer<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getName().equals("Routes.php") || file.getName().equals("AjaxRoutes.php") || file.getName().equals("ext_tables.php");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    static class ExtTablesRouteVisitor extends PsiRecursiveElementVisitor {
        List<RouteStub> routeStubs;

        public ExtTablesRouteVisitor() {
            routeStubs = new ArrayList<>();
        }

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof MethodReference)) {
                super.visitElement(element);
                return;
            }

            MethodReference methodReference = (MethodReference) element;
            PhpExpression classRefExpr = methodReference.getClassReference();
            if (!(classRefExpr instanceof ClassReference)) {
                super.visitElement(element);
                return;
            }

            ClassReference classReference = (ClassReference) classRefExpr;

            if (classReference.getFQN().equals("\\TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility") && methodReference.getName().equals("addModule")) {
                RouteStub e = extractRouteStubFromMethodCall(methodReference);
                if (e != null) {
                    routeStubs.add(e);
                }
            }
        }

        private RouteStub extractRouteStubFromMethodCall(MethodReference methodReference) {
            RouteStub routeStub = new RouteStub();
            PsiElement[] parameters = methodReference.getParameters();
            if (!(parameters.length >= 5)) {
                return null;
            }

            PsiElement psiElement = Arrays.asList(parameters).get(4);
            if (!(psiElement instanceof ArrayCreationExpression)) {
                return null;
            }

            ArrayCreationExpression routeArray = (ArrayCreationExpression) psiElement;
            for (ArrayHashElement arrayHashElement : routeArray.getHashElements()) {
                PhpPsiElement key = arrayHashElement.getKey();
                if (key == null || !(key instanceof StringLiteralExpression)) {
                    continue;
                }

                StringLiteralExpression key1 = (StringLiteralExpression) key;
                if (key1.getContents().equals("routeTarget") && (arrayHashElement.getValue() instanceof ConcatenationExpression)) {
                    ConcatenationExpression value = (ConcatenationExpression) arrayHashElement.getValue();

                    for (PsiElement element : value.getChildren()) {
                        if (element instanceof ClassConstantReference) {
                            PhpExpression classReference = ((ClassConstantReference) element).getClassReference();
                            if (classReference instanceof ClassReference) {
                                routeStub.setController(((ClassReference) classReference).getFQN());
                            }
                        }

                        if (element instanceof StringLiteralExpression) {
                            routeStub.setMethod(((StringLiteralExpression) element).getContents().replace("::", ""));
                        }
                    }

                }

                if (key1.getContents().equals("access") && (arrayHashElement.getValue() instanceof StringLiteralExpression)) {
                    StringLiteralExpression value = (StringLiteralExpression) arrayHashElement.getValue();
                    routeStub.setAccess(value.getContents());
                }

                if (key1.getContents().equals("name") && (arrayHashElement.getValue() instanceof StringLiteralExpression)) {
                    StringLiteralExpression value = (StringLiteralExpression) arrayHashElement.getValue();

                    routeStub.setName(value.getContents());
                    routeStub.setTextRange(value.getTextRange());
                }
            }

            if (routeStub.getName() == null) {
                return null;
            }

            return routeStub;
        }

        List<RouteStub> getRouteStubs() {
            return routeStubs;
        }
    }
}
