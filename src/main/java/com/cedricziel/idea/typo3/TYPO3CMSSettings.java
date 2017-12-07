package com.cedricziel.idea.typo3;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "TYPO3Settings",
        storages = {@Storage("$WORKSPACE_FILE$")}
)
public class TYPO3CMSSettings implements PersistentStateComponent<Element> {
    /**
     * Plugin version.
     */
    private String version;

    public static TYPO3CMSSettings getInstance(Project project) {
        return ServiceManager.getService(project, TYPO3CMSSettings.class);
    }

    @Nullable
    @Override
    public Element getState() {
        final Element element = new Element(KEY.ROOT.toString());
        element.setAttribute(KEY.VERSION.toString(), version);

        return element;
    }

    @Override
    public void loadState(Element state) {

        String value = state.getAttributeValue(KEY.VERSION.toString());
        if (value != null) {
            version = value;
        }

    }

    /**
     * Returns plugin version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets plugin version.
     *
     * @param version of the plugin
     */
    public void setVersion(@NotNull String version) {
        this.version = version;
    }

    /**
     * Settings keys.
     */
    public enum KEY {
        ROOT("TYPO3CMSSettings"),
        VERSION("version");

        private final String key;

        KEY(@NotNull String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

}
