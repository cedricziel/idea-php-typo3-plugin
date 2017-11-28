package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.index.externalizer.ObjectStreamDataExternalizer;
import com.cedricziel.idea.typo3.routing.RouteHelper;
import com.cedricziel.idea.typo3.routing.RouteStub;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

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

            for (RouteStub route : RouteHelper.routesFromRoutesPhp(inputData.getPsiFile())) {
                map.put(route.getName(), route);
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
        return file -> file.getName().equals("Routes.php") || file.getName().equals("AjaxRoutes.php");
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
