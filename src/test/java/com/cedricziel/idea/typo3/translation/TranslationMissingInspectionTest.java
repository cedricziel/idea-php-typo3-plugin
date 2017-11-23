package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.BaseLightCodeInsightFixtureTestCase;

public class TranslationMissingInspectionTest extends BaseLightCodeInsightFixtureTestCase {

    public void testMissingTranslationKeyInspection() {
        myFixture.addFileToProject("typo3/sysext/baz/bar.png", "");

        myFixture.addFileToProject("typo3/sysext/baz/bar.xml", "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>\n" +
                "<T3locallang>\n" +
                "\t<meta type=\"array\">\n" +
                "\t\t<type>CSH</type>\n" +
                "\t\t<description>Language labels for the blog_example context sensitive help (CSH)</description>\n" +
                "\t\t<csh_table>_MOD_txblogexampleM1</csh_table>\n" +
                "\t</meta>\n" +
                "\t<data type=\"array\">\n" +
                "\t\t<languageKey index=\"default\" type=\"array\">\n" +
                "\t\t\t<label index=\".alttitle\">Blog Example</label>\n" +
                "\t\t\t<label index=\".description\">This is some dummy help. But it's context sensitive!</label>\n" +
                "\t\t\t<label index=\".seeAlso\">_MOD_web_layout:columns_single</label>\n" +
                "\t\t</languageKey>\n" +
                "\t</data>\n" +
                "</T3locallang>");

        assertLocalInspectionContains("foo.php", "<?php\n" +
                        "return 'LLL:EXT:baz/bar.xml:s<caret>eeAlso';",
                "Create Translation key");
    }
}
