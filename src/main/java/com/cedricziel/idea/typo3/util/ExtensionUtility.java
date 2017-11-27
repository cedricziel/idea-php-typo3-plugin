package com.cedricziel.idea.typo3.util;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.roots.PhpNamespaceCompositeProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExtensionUtility {

    public static PsiDirectory getExtensionDirectory(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        DataContext dataContext = event.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return null;
        }

        PsiDirectory[] directories = view.getDirectories();
        if (directories.length == 0) {
            return null;
        }

        return FilesystemUtil.findParentExtensionDirectory(directories[0]);
    }

    @Nullable
    public static String findDefaultNamespace(@NotNull PsiDirectory extensionRootDirectory) {
        PsiDirectory classesDirectory = extensionRootDirectory.findSubdirectory("Classes");
        if (classesDirectory == null) {
            return null;
        }

        VirtualFile composerManifest = ExtensionUtility.findComposerManifest(extensionRootDirectory);
        if (composerManifest != null) {
            String[] namespaces = ComposerUtil.findNamespaces(composerManifest);
            if (namespaces.length != 0) {
                return namespaces[0];
            }
        }

        PsiFile extEmConf = findExtEmConfPsiFile(extensionRootDirectory);
        if (extEmConf != null) {
            String[] namespaces = ExtensionUtility.extractPsr4NamespacesFromExtEmconf(extEmConf);
            if (namespaces.length != 0) {
                return namespaces[0];
            }
        }

        List<String> strings = PhpNamespaceCompositeProvider.INSTANCE.suggestNamespaces(classesDirectory);
        if (strings.size() == 0 || strings.get(0).isEmpty()) {
            return null;
        }

        return strings.get(0);
    }

    private static String[] extractPsr4Namespaces(@NotNull VirtualFile extEmConf) {
        return new String[0];
    }

    private static String[] extractPsr4NamespacesFromExtEmconf(@NotNull PsiFile extEmConf) {
        ExtEmconfNamespacesVisitor phpElementVisitor = new ExtEmconfNamespacesVisitor();

        phpElementVisitor.visitFile(extEmConf);

        return phpElementVisitor.getNamespaces();
    }

    private static VirtualFile findExtEmConf(@NotNull PsiDirectory extensionRootDirectory) {
        PsiFile file = extensionRootDirectory.findFile("ext_emconf.php");
        if (file == null) {
            return null;
        }

        return file.getVirtualFile();
    }

    private static PsiFile findExtEmConfPsiFile(@NotNull PsiDirectory extensionRootDirectory) {
        return extensionRootDirectory.findFile("ext_emconf.php");
    }

    private static VirtualFile findComposerManifest(@NotNull PsiDirectory extensionRootDirectory) {
        PsiFile file = extensionRootDirectory.findFile("composer.json");
        if (file == null) {
            return null;
        }

        return file.getVirtualFile();
    }

    private static class ExtEmconfNamespacesVisitor extends PsiRecursiveElementVisitor {
        private List<String> ns;

        @Override
        public void visitElement(PsiElement element) {

            if (PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withParent(
                            PlatformPatterns.psiElement(ArrayHashElement.class).withParent(
                                    PlatformPatterns.psiElement(ArrayCreationExpression.class).withParent(
                                            PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).withParent(
                                                    PlatformPatterns.psiElement(ArrayHashElement.class).withFirstChild(
                                                            PlatformPatterns.or(
                                                                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withText("'psr-4'"),
                                                                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withText("\"psr-4\"")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            ).accepts(element)) {
                if (ns == null) {
                    ns = new ArrayList<>();
                }

                String contents = ((StringLiteralExpression) element).getContents();
                if (contents.contains("\\")) {
                    contents = contents.replace("\\\\", "\\");
                }

                if (contents.endsWith("\\")) {
                    ns.add(contents);
                }

                ns.add((contents + "\\"));
            }

            super.visitElement(element);
        }

        public String[] getNamespaces() {
            if (ns == null) {
                return new String[0];
            }

            return ns.toArray(new String[ns.size()]);
        }
    }
}
