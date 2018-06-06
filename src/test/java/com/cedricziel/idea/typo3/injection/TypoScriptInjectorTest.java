package com.cedricziel.idea.typo3.injection;

import com.cedricziel.idea.typo3.AbstractTestCase;
import de.sgalinski.typoscript.language.TypoScriptLanguage;

public class TypoScriptInjectorTest extends AbstractTestCase {
    public void testInjectsLanguagesInMethodArguments() {
        myFixture.configureByText("foo.php", "<?php \n" +
                "\\TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::addTypoScriptConstants(\"<caret>\">)");

        assertInstanceOf(myFixture.getElementAtCaret().getLanguage(), TypoScriptLanguage.class);
    }
}
