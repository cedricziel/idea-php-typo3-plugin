package com.cedricziel.idea.typo3.container;

import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.cedricziel.idea.typo3.psi.visitor.CoreIconParserVisitor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconProvider {

    public static String ICON_REGISTRY_CLASS = "TYPO3\\CMS\\Core\\Imaging\\IconRegistry";
    private static IconProvider instance;

    private Map<String, List<TYPO3IconDefinition>> icons;

    public IconProvider() {
        // Initialize with a larger capacity since core alone has around 400 definitions
        this.icons = new HashMap<>(500);
    }

    public void collect(Project project) {
        collectIcons(project);
        collectImagesForIcons(project);
    }

    private void collectImagesForIcons(Project project) {

        VfsUtil.processFileRecursivelyWithoutIgnored(
                project.getBaseDir(),
                virtualFile -> {
                    if (!"png".equalsIgnoreCase(virtualFile.getExtension()) && !"svg".equalsIgnoreCase(virtualFile.getExtension())) {
                        return true;
                    }

                    getElementMap().forEach((psiElement, typo3IconDefinition) -> {
                        if (typo3IconDefinition.getSource() == null) {
                            return;
                        }
                        String filePath = typo3IconDefinition.getSource().substring(4);
                        if (virtualFile.getPath().contains(filePath)) {
                            typo3IconDefinition.setVirtualFile(virtualFile);
                        }
                    });

                    return true;
                }
        );
    }

    private void collectIcons(Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        Collection<PhpClass> iconRegistry = phpIndex.getClassesByFQN(ICON_REGISTRY_CLASS);
        if (iconRegistry.isEmpty()) {
            return;
        }

        iconRegistry.forEach(iconRegistryClass -> {
            Collection<Field> fields = iconRegistryClass.getFields();
            fields.forEach(field -> {
                if ("icons".equals(field.getName())) {
                    field.accept(new CoreIconParserVisitor(icons));
                }
            });
        });
    }

    public Map<PsiElement, TYPO3IconDefinition> getElementMap() {
        Map<PsiElement, TYPO3IconDefinition> map = new HashMap<>(icons.size());
        icons.forEach((key, iconList) -> {
            if (iconList == null || iconList.isEmpty()) {
                return;
            }

            map.put(iconList.iterator().next().getElement(), iconList.iterator().next());
        });

        return map;
    }

    public static IconProvider getInstance(Project project) {
        if (instance == null) {
            instance = new IconProvider();
            instance.collect(project);
            instance.collectImagesForIcons(project);
        }

        return instance;
    }
}
