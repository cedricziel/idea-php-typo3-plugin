package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilesystemUtil {
    public static PsiDirectory[] findParentExtensionDirectory(@NotNull PsiDirectory directory) {

        return new PsiDirectory[0];
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
