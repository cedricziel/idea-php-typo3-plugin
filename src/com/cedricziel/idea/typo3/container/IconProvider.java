package com.cedricziel.idea.typo3.container;

import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.cedricziel.idea.typo3.psi.visitor.CoreIconParserVisitor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.*;

public class IconProvider {

    public static String ICON_REGISTRY_CLASS = "TYPO3\\CMS\\Core\\Imaging\\IconRegistry";
    private static IconProvider instance;

    private Map<Project, Map<String, List<TYPO3IconDefinition>>> icons = new HashMap<>();

    public static synchronized IconProvider getInstance(Project project) {
        if (instance == null) {
            instance = new IconProvider();
        }

        if (!instance.icons.containsKey(project) || instance.icons.get(project) == null) {
            instance.initialize(project);
            instance.collect(project);
        }

        return instance;
    }

    private void initialize(Project project) {
        // Initialize with a larger capacity since core alone has around 400 definitions
        this.icons.put(project, new HashMap<>(500));
    }

    public static void destroyInstance(Project project) {
        getInstance(project).destroy(project);
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

                    getElementMap(project).forEach((psiElement, typo3IconDefinition) -> {
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
                    field.accept(new CoreIconParserVisitor(icons.get(project)));
                }
            });
        });
    }

    public Map<PsiElement, TYPO3IconDefinition> getElementMap(Project project) {
        Map<String, List<TYPO3IconDefinition>> projectIconMap = icons.get(project);
        Map<PsiElement, TYPO3IconDefinition> map = new HashMap<>(projectIconMap.size());
        projectIconMap.forEach((key, iconList) -> {
            if (iconList == null || iconList.isEmpty()) {
                return;
            }

            map.put(iconList.iterator().next().getElement(), iconList.iterator().next());
        });

        return map;
    }

    public boolean has(Project project, String value) {
        return icons.get(project).containsKey(value);
    }

    public List<TYPO3IconDefinition> get(Project project, String value) {
        return icons.get(project).get(value);
    }

    public List<TYPO3IconDefinition> all(Project project) {
        if (!icons.containsKey(project) || icons.get(project) == null) {
            return new ArrayList<>();
        }

        List<TYPO3IconDefinition> list = new ArrayList<>();
        icons.get(project).forEach((icon, definitions) -> list.addAll(definitions));

        return list;
    }

    private void destroy(Project project) {
        if (this.icons.containsKey(project)) {
            this.icons.remove(project);
        }
    }
}
