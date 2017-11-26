package com.cedricziel.idea.typo3.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.ComposerConfigUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ComposerUtil {
    @Nullable
    public static String extensionKeyFromPackageName(JsonElement jsonElement) {
        JsonElement nameElement = jsonElement.getAsJsonObject().get("name");
        if (nameElement != null) {
            String name = nameElement.getAsString();
            if (name != null) {
                if (name.contains("/")) {
                    return name.split("/")[1].replaceAll("-", "_");
                }
            }
        }

        return null;
    }

    public static String extensionKeyFromInstallerName(JsonElement jsonElement) {

        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (!asJsonObject.has("extra")) {
            return null;
        }

        JsonObject extra = asJsonObject.get("extra").getAsJsonObject();
        if (!extra.has("installerName")) {
            return null;
        }

        return extra.getAsJsonObject().getAsJsonPrimitive("installerName").getAsString();
    }

    public static String extensionKeyFromExtraExtensionKey(JsonElement jsonElement) {

        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (!asJsonObject.has("extra")) {
            return null;
        }

        JsonObject extra = asJsonObject.get("extra").getAsJsonObject();
        if (!extra.has("typo3/cms")) {
            return null;
        }

        JsonObject typo3Extras = extra.getAsJsonObject("typo3/cms");
        if (!typo3Extras.has("extension-key")) {
            return null;
        }

        return typo3Extras.getAsJsonPrimitive("extension-key").getAsString();
    }

    public static String packageType(JsonElement jsonElement) {

        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (!asJsonObject.has("type")) {
            return null;
        }

        return asJsonObject.get("type").getAsJsonPrimitive().getAsString();
    }

    public static boolean isTYPO3ExtensionManifest(JsonElement jsonElement) {

        String packageType = packageType(jsonElement);
        if (packageType == null) {
            return false;
        }

        return packageType.equals("typo3-cms-extension");
    }

    @Nullable
    public static String findExtensionKey(@NotNull VirtualFile composerJsonFile) {
        try {
            JsonElement jsonElement = ComposerConfigUtils.parseJson(composerJsonFile);

            if (!isTYPO3ExtensionManifest(jsonElement)) {
                return null;
            }

            // 1.1 find extra.typo3/cms.extension-key
            String extensionKeyFromExtrasTYPO3 = extensionKeyFromExtraExtensionKey(jsonElement);
            if (extensionKeyFromExtrasTYPO3 != null) {
                return extensionKeyFromExtrasTYPO3;
            }

            // 1.2 find extra.installerName
            String extensionKeyFromInstallerName = extensionKeyFromInstallerName(jsonElement);
            if (extensionKeyFromInstallerName != null) {
                return extensionKeyFromInstallerName;
            }

            // 1.3 last resort: package-name
            String extensionKeyFromPackageName = extensionKeyFromPackageName(jsonElement);
            if (extensionKeyFromPackageName != null) {
                return extensionKeyFromPackageName;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException | IllegalStateException e) {
            // nothing, yo
        }

        return null;
    }

    @NotNull
    public static String[] findNamespaces(@NotNull VirtualFile composerJsonFile) {
        try {
            JsonElement jsonElement = ComposerConfigUtils.parseJson(composerJsonFile);

            if (!isTYPO3ExtensionManifest(jsonElement)) {
                return new String[0];
            }

            return psr4Roots(jsonElement);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    @NotNull
    private static String[] psr4Roots(JsonElement composerJsonFile) {
        JsonObject asJsonObject = composerJsonFile.getAsJsonObject();
        if (!asJsonObject.has("autoload")) {
            return new String[0];
        }

        JsonObject autoloadHash = asJsonObject.get("autoload").getAsJsonObject();
        if (!autoloadHash.has("psr-4")) {
            return new String[0];
        }

        JsonObject psr4Hash = autoloadHash.get("psr-4").getAsJsonObject();

        return psr4Hash
                .entrySet()
                .stream()
                .filter(Objects::nonNull)
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }
}
