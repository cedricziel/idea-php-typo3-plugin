package com.cedricziel.idea.typo3.icons;

import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;

public class IconStub implements IconInterface {

    private final SmartPsiElementPointer<PsiElement> element;
    private String identifier;
    private String filename;
    private String provider;
    private String source;

    public IconStub(String identifier, PsiElement element) {
        this.identifier = identifier;
        this.element = SmartPointerManager.createPointer(element);
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

    public PsiElement getElement() {
        return element.getElement();
    }
}
