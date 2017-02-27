package com.cedricziel.idea.typo3.domain.factory;

import com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileSystemEntry;
import com.jetbrains.php.composer.ComposerConfigUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import static com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition.COMPOSER_TYPE_CMS_EXTENSION;
import static com.cedricziel.idea.typo3.domain.TYPO3ExtensionDefinition.COMPOSER_TYPE_CMS_FRAMEWORK;

public class ExtensionDefinitionFactory {

    public static TYPO3ExtensionDefinition fromDirectory(VirtualDirectoryImpl virtualDirectory) {
        TYPO3ExtensionDefinition extensionDefinition = null;

        // try finding composer manifest
        VirtualFileSystemEntry composerManifest = virtualDirectory.findChild("composer.json");
        if (composerManifest != null) {
            try {
                extensionDefinition = fromComposerManifest(composerManifest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return extensionDefinition != null ? extensionDefinition : null;
    }

    /**
     * Reads a composer manifest and compiles it into a {@link TYPO3ExtensionDefinition}, if possible.
     *
     * @param composerManifest The manifest virtual file system entry to parse
     * @return The derived extension definition.
     * @throws IOException thrown if reading the manifest fails.
     */
    private static TYPO3ExtensionDefinition fromComposerManifest(VirtualFile composerManifest) throws IOException {
        TYPO3ExtensionDefinition extensionDefinition = new TYPO3ExtensionDefinition();
        extensionDefinition.setRootDirectory(composerManifest.getParent());

        JsonElement jsonElement = ComposerConfigUtils.parseJson(composerManifest);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String packageName = "";
            String composerPackageType = "";
            String extensionKey = "";

            JsonElement nameElement = jsonObject.get("name");
            if (nameElement == null) {
                return null;
            }

            packageName = nameElement.getAsString();

            JsonElement typeElement = jsonObject.get("type");
            if (typeElement.isJsonPrimitive()) {
                composerPackageType = typeElement.getAsString();
                if (!isValidPackageType(composerPackageType)) {
                    return null;
                }
            }

            extensionDefinition.setPackageName(packageName);

            /*
              Here we either extract the extension key from the composer manifest or
              guess the extension key from the package name.
             */
            if (jsonObject.has("extra") && !extractExtensionKeyFromComposerExtras(jsonObject.getAsJsonObject()).isEmpty()) {
                extensionKey = extractExtensionKeyFromComposerExtras(jsonObject.getAsJsonObject());
            } else {
                extensionKey = extensionKeyFromPackageName(packageName);
                if (composerPackageType.equals(COMPOSER_TYPE_CMS_FRAMEWORK)) {
                    if ("cms_".equals(extensionKey.substring(0, 4))) {
                        extensionKey = extensionKey.substring(4);
                    }
                }
            }
            extensionDefinition.setExtensionKey(extensionKey);
        }

        return extensionDefinition;
    }

    /**
     * Tries to extract the extensionKey from composer extras. Expects the following data structur:
     * <p>
     * <pre>
     *     "extra": {
     *        "installer-name": "formengine_map",
     *        "typo3/cms": {
     *           "extensionKey": "formengine_map"
     *        }
     *     }
     * </pre>
     *
     * @param composerExtrasObject A json object with the structure of composer extras
     * @return The extension key if properly extracted or an empty string
     */
    private static String extractExtensionKeyFromComposerExtras(JsonObject composerExtrasObject) {
        if (composerExtrasObject.has("installer-name") && !composerExtrasObject.get("installer-name").getAsString().isEmpty()) {
            return composerExtrasObject.get("installer-name").getAsString();
        }

        if (composerExtrasObject.has("typo3/cms") && composerExtrasObject.get("typo3/cms").isJsonObject()) {
            JsonObject typo3cmsElement = composerExtrasObject.get("typo3/cms").getAsJsonObject();
            if (typo3cmsElement.has("extensionKey") && !typo3cmsElement.get("extensionKey").getAsString().isEmpty()) {
                return typo3cmsElement.get("extensionKey").getAsString();
            }
        }

        return "";
    }

    /**
     * TYPO3 CMS Extensions should only ever have either type "typo3-cms-framework" or "typo3-cms-extension".
     *
     * @param composerPackageType The package type to test
     * @return Whether the package key is valid.
     */
    private static boolean isValidPackageType(String composerPackageType) {

        return !composerPackageType.isEmpty()
                && (composerPackageType.equals(COMPOSER_TYPE_CMS_EXTENSION) || composerPackageType.equals(COMPOSER_TYPE_CMS_FRAMEWORK));
    }

    /**
     * Guesses an extension key from the composer package name.
     *
     * @param packageName The package name to analyze
     * @return
     */
    private static String extensionKeyFromPackageName(String packageName) {

        String[] split = StringUtils.split(packageName, "/");
        if (split.length < 2) {
            return packageName;
        }

        return StringUtils
                .lowerCase(split[1])
                .replace("-", "_");
    }
}
