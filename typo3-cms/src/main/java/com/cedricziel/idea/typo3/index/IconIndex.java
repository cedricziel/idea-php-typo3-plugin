package com.cedricziel.idea.typo3.index;

import com.cedricziel.idea.typo3.icons.IconStub;
import com.cedricziel.idea.typo3.psi.visitor.CoreFlagParserVisitor;
import com.cedricziel.idea.typo3.psi.visitor.CoreIconParserVisitor;
import com.intellij.openapi.file.exclude.EnforcedPlainTextFileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cedricziel.idea.typo3.util.IconUtil.ICON_REGISTRY_CLASS;

public class IconIndex extends ScalarIndexExtension<String> {

    private static final Key<CachedValue<Map<String, IconStub>>> TYPO3_CMS_ICON_USAGES = new Key<>("TYPO3_CMS_ICON_USAGES");
    private static final Key<CachedValue<IconStub[]>> TYPO3_CMS_PROJECT_ICONS = new Key<>("TYPO3_CMS_PROJECT_ICONS");

    public static ID<String, Void> KEY = ID.create("com.cedricziel.idea.typo3.index.icon");

    @NotNull
    public static Collection<String> getAllAvailableIcons(@NotNull Project project) {

        return FileBasedIndex.getInstance().getAllKeys(KEY, project);
    }

    public static Collection<IconStub> getIcon(@NotNull Project project, @NotNull String iconIdentifier) {
        List<IconStub> icons = new ArrayList<>();

        for (IconStub icon : getAllIcons(project)) {
            if (icon.getIdentifier().equals(iconIdentifier)) {
                icons.add(icon);
            }
        }

        return icons;
    }

    @NotNull
    public static IconStub[] getAllIcons(@NotNull Project project) {

        CachedValue<IconStub[]> value = project.getUserData(TYPO3_CMS_PROJECT_ICONS);
        if (value != null && value.hasUpToDateValue()) {
            return value.getValue();
        }

        CachedValue<IconStub[]> cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
            return CachedValueProvider.Result.create(getAllIconsUncached(project), PsiModificationTracker.MODIFICATION_COUNT);
        }, false);

        project.putUserData(TYPO3_CMS_PROJECT_ICONS, cachedValue);

        return cachedValue.getValue();
    }

    @NotNull
    private static IconStub[] getAllIconsUncached(@NotNull Project project) {
        Map<String, IconStub> iconStubs = new THashMap<>();

        FileBasedIndex.getInstance().getAllKeys(IconIndex.KEY, project).forEach(k -> {
            FileBasedIndex.getInstance().getFilesWithKey(IconIndex.KEY, ContainerUtil.set(k), v -> {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(v);
                if (psiFile instanceof PhpFile) {
                    visitPhpFile(iconStubs, (PhpFile) psiFile);
                }

                return true;
            }, GlobalSearchScope.allScope(project));
        });

        return iconStubs.values().toArray(new IconStub[0]);
    }

    public static boolean hasIcon(@NotNull Project project, @NotNull String iconIdentifier) {
        AtomicBoolean iconExists = new AtomicBoolean(false);

        FileBasedIndex.getInstance().processAllKeys(IconIndex.KEY, s -> {
            if (s.equals(iconIdentifier)) {
                iconExists.set(true);

                return false;
            }

            return true;
        }, project);

        return iconExists.get();
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            Map<String, IconStub> iconIdentifiers = new THashMap<>();

            // index the icon registry
            PsiFile psiFile = inputData.getPsiFile();
            if (psiFile instanceof PhpFile) {
                visitPhpFile(iconIdentifiers, (PhpFile) psiFile);
            }

            Map<String, Void> result = new THashMap<>();
            iconIdentifiers.forEach((k, v) -> result.put(k, null));

            return result;
        };
    }

    private static void visitPhpFile(Map<String, IconStub> iconIdentifiers, PhpFile psiFile) {
        CachedValue<Map<String, IconStub>> userData = psiFile.getUserData(TYPO3_CMS_ICON_USAGES);
        if (userData != null && userData.hasUpToDateValue()) {
            iconIdentifiers.putAll(userData.getValue());

            return;
        }

        CachedValue<Map<String, IconStub>> cachedValue = CachedValuesManager.getManager(psiFile.getProject()).createCachedValue(() -> {
            Map<String, IconStub> cacher = new THashMap<>();

            visitIconRegistry(cacher, psiFile);

            return CachedValueProvider.Result.create(cacher, PsiModificationTracker.MODIFICATION_COUNT);
        }, false);

        psiFile.putUserData(TYPO3_CMS_ICON_USAGES, cachedValue);
        iconIdentifiers.putAll(cachedValue.getValue());
    }

    private static void visitIconRegistry(Map<String, IconStub> iconIdentifiers, PhpFile phpFile) {
        PhpClass iconRegistryClass = PhpPsiUtil.findClass(phpFile, phpClass -> phpClass.getPresentableFQN().equals(ICON_REGISTRY_CLASS));
        if (iconRegistryClass == null) {
            return;
        }

        CoreIconParserVisitor iconVisitor = new CoreIconParserVisitor();
        iconRegistryClass.accept(iconVisitor);

        iconVisitor.getMap().forEach(iconIdentifiers::put);

        CoreFlagParserVisitor flagVisitor = new CoreFlagParserVisitor();
        iconRegistryClass.accept(flagVisitor);

        flagVisitor.getMap().forEach(iconIdentifiers::put);
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            String extension = file.getExtension();

            boolean isEnforcedPlaintext = EnforcedPlainTextFileTypeManager.getInstance().isMarkedAsPlainText(file);

            return extension != null && extension.equalsIgnoreCase("php") && !isEnforcedPlaintext;
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
