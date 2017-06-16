package com.cedricziel.idea.typo3.domain;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;

public class TYPO3IconDefinition {
    private String identifier;
    private String extension;
    private PsiElement element;
    private String filename;
    private ClassConstantReference provider;
    private String source;
    private VirtualFile virtualFile;

    public TYPO3IconDefinition(ClassConstantReference provider, String identifier, String source, PsiElement element) {
        this.provider = provider;
        this.identifier = identifier;
        this.source = source;
        this.element = element;
    }

    public TYPO3IconDefinition() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public PsiElement getElement() {
        return element;
    }

    public void setElement(PsiElement element) {
        this.element = element;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public ClassConstantReference getProvider() {
        return provider;
    }

    public void setProvider(ClassConstantReference provider) {
        this.provider = provider;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }
}
