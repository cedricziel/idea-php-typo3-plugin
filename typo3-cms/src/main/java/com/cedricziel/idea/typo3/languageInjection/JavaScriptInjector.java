package com.cedricziel.idea.typo3.languageInjection;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlElementType;
import org.jetbrains.annotations.NotNull;

public class JavaScriptInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (getXmlFAssetJsContentPattern().accepts(host)) {
            injectionPlacesRegistrar.addPlace(JavascriptLanguage.INSTANCE, new TextRange(0, host.getTextLength()-1), null, null);
        }
    }

    @NotNull
    private PsiElementPattern.Capture<PsiElement> getXmlFAssetJsContentPattern() {
        return PlatformPatterns.psiElement(XmlElementType.XML_TEXT).withParent(
            XmlPatterns.xmlTag().withName("f:asset.script")
        );
    }
}
