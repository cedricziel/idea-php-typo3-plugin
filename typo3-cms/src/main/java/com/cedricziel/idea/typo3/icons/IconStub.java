package com.cedricziel.idea.typo3.icons;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.io.Serializable;

public class IconStub implements IconInterface, Serializable {

    private String identifier;
    private String extension;
    private String filename;
    private String provider;
    private String source;

    private TextRange textRange;

    public IconStub(String identifier, PsiElement element) {
        this.identifier = identifier;

        this.textRange = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public String getExtensionKey() {
        return null;
    }

    @Override
    public String getProvider() {
        return null;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IconStub iconStub = (IconStub) o;

        if (!identifier.equals(iconStub.identifier)) return false;
        if (extension != null ? !extension.equals(iconStub.extension) : iconStub.extension != null) return false;
        if (filename != null ? !filename.equals(iconStub.filename) : iconStub.filename != null) return false;
        if (provider != null ? !provider.equals(iconStub.provider) : iconStub.provider != null) return false;
        if (source != null ? !source.equals(iconStub.source) : iconStub.source != null) return false;
        return textRange.equals(iconStub.textRange);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + textRange.hashCode();
        return result;
    }
}
