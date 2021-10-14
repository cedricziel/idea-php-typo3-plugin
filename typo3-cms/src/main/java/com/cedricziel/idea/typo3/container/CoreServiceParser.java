package com.cedricziel.idea.typo3.container;

import com.cedricziel.idea.typo3.domain.TYPO3ServiceDefinition;
import com.cedricziel.idea.typo3.psi.visitor.CoreServiceDefinitionParserVisitor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.lang.PhpFileType;

import java.util.*;

/**
 * Parses the core services to a map. Core Services are not traditional
 * services in a DIC, but rather named elements in a service locator.
 */
public class CoreServiceParser {

    private final HashMap<String, ArrayList<TYPO3ServiceDefinition>> serviceMap;

    public CoreServiceParser() {
        this.serviceMap = new HashMap<>();
    }

    private void collectServices(Project project) {
        Collection<VirtualFile> containingFiles = FileTypeIndex.getFiles(PhpFileType.INSTANCE, GlobalSearchScope.allScope(project));

        containingFiles.removeIf(virtualFile -> !(virtualFile.getName().contains("ext_localconf.php")));

        for (VirtualFile projectFile : containingFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(projectFile);
            if (psiFile != null) {
                CoreServiceDefinitionParserVisitor visitor = new CoreServiceDefinitionParserVisitor(serviceMap);
                psiFile.accept(visitor);

                serviceMap.putAll(visitor.getServiceMap());
            }
        }
    }

    public Boolean has(Project project, String serviceId) {
        return this.serviceMap.containsKey(serviceId);
    }

    public List<TYPO3ServiceDefinition> resolve(Project project, String serviceId) {
        return this.serviceMap.get(serviceId);
    }

    public void collect(Project project) {
        collectServices(project);
    }
}
