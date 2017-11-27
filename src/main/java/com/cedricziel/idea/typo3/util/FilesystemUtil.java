package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilesystemUtil {
    @Nullable
    public static PsiDirectory findParentExtensionDirectory(@NotNull PsiDirectory directory) {

        VirtualFile extensionRootFolder = findExtensionRootFolder(directory.getVirtualFile());
        if (extensionRootFolder == null) {
            return null;
        }

        return PsiManager.getInstance(directory.getProject()).findDirectory(extensionRootFolder);
    }

    @Nullable
    public static VirtualFile findExtensionRootFolder(@NotNull VirtualFile file) {
        if (file.isDirectory()) {
            VirtualFile child = file.findChild("ext_emconf.php");

            if (child != null) {
                return file;
            }
        }

        // dragons ahead.
        if (file.getParent() != null) {
            return findExtensionRootFolder(file.getParent());
        }

        return null;
    }
}
