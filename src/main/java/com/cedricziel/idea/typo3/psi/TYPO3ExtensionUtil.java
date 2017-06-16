package com.cedricziel.idea.typo3.psi;

import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.cedricziel.idea.typo3.domain.factory.ExtensionDefinitionFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiDirectory;

public class TYPO3ExtensionUtil {
    /**
     * Traverses the given directories and returns the first valid
     * extension definition that's applicable.
     *
     * @param directories List of directories to analyze
     */
    public static TYPO3ExtensionDefinition findContainingExtension(PsiDirectory[] directories) {
        for (PsiDirectory directory : directories) {
            VirtualDirectoryImpl virtualFile = (VirtualDirectoryImpl) directory.getVirtualFile();

            while (!isExtensionRootDirectory(virtualFile)) {
                if (virtualFile.getParent() == null) {
                    return null;
                }

                virtualFile = virtualFile.getParent();
            }

            TYPO3ExtensionDefinition extensionDefinition = ExtensionDefinitionFactory.fromDirectory(virtualFile);
            if (extensionDefinition != null) {
                return extensionDefinition;
            }
        }

        return null;
    }

    /**
     * Determines if a directory is the top-most directory of an extension.
     * It does so by searching the "ext_emconf.php"
     *
     * @param virtualFile Directory to scan
     * @return true if the current directory is a root directory.
     */
    private static boolean isExtensionRootDirectory(VirtualDirectoryImpl virtualFile) {
        VirtualFile[] immediateChildren = virtualFile.getChildren();
        for (VirtualFile file : immediateChildren) {
            if (file.getName().equals("ext_emconf.php")) {
                return true;
            }
        }
        return false;
    }
}
