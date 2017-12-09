package com.cedricziel.idea.typo3.translation;

import com.intellij.openapi.util.TextRange;

import java.io.Serializable;
import java.util.Objects;

public class StubTranslation implements TranslationInterface, Serializable {
    private String id;
    private TextRange textRange;
    private String extension;
    private String index;
    private String language;

    public StubTranslation(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StubTranslation that = (StubTranslation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(textRange, that.textRange) &&
                Objects.equals(extension, that.extension) &&
                Objects.equals(index, that.index) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, textRange, extension, index, language);
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(TextRange textRange) {
        this.textRange = textRange;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
