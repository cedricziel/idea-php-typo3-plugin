package com.cedricziel.idea.typoscript.lang;

import com.intellij.lang.Language;

public class TypoScriptLanguage extends Language {
    public static final TypoScriptLanguage INSTANCE = new TypoScriptLanguage();

    private TypoScriptLanguage() {
        super("TypoScript");
    }
}
