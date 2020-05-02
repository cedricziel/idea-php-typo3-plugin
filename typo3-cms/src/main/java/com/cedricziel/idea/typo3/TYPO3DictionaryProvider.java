package com.cedricziel.idea.typo3;

import com.intellij.spellchecker.BundledDictionaryProvider;

public class TYPO3DictionaryProvider implements BundledDictionaryProvider {
    @Override
    public String[] getBundledDictionaries() {
        return new String[]{"typo3.dic"};
    }
}
