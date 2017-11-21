package com.cedricziel.idea.typo3.index;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class TranslationIndexTest extends LightCodeInsightFixtureTestCase {
    public void testResourcesAreIndexed() {
        myFixture.addFileToProject("typo3conf/ext/foo/bar.php", "");
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

        myFixture.addFileToProject("typo3/sysext/baz/bar.xlf", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<xliff version=\"1.0\" xmlns:t3=\"http://typo3.org/schemas/xliff\">\n" +
                "\t<file t3:id=\"1415814842\" source-language=\"en\" datatype=\"plaintext\" original=\"messages\" date=\"2011-10-17T20:22:33Z\" product-name=\"install\">\n" +
                "\t\t<header/>\n" +
                "\t\t<body>\n" +
                "\t\t\t<trans-unit id=\"mlang_labels_tablabel\">\n" +
                "\t\t\t\t<source>Install&#160;Tool</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t\t<trans-unit id=\"mlang_labels_tabdescr\">\n" +
                "\t\t\t\t<source>Provides a simple link to the Install&#160;Tool. Notice the Install&#160;Tool (in typo3/install/) must be enabled.&lt;br /&gt;&lt;em&gt;Access for 'admin' users only!&lt;/em&gt;</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t\t<trans-unit id=\"mlang_tabs_tab\">\n" +
                "\t\t\t\t<source>Install</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t\t<trans-unit id=\"confirmUnlockInstallToolTitle\">\n" +
                "\t\t\t\t<source>The Install&#160;Tool is locked</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t\t<trans-unit id=\"confirmUnlockInstallToolMessage\" xml:space=\"preserve\">\n" +
                "\t\t\t\t<source>&lt;p&gt;For security reasons the Install&#160;Tool is locked by default.&lt;/p&gt;\n" +
                "&lt;p&gt;To enable the Install&#160;Tool, the file &lt;strong&gt;ENABLE_INSTALL_TOOL&lt;/strong&gt; must be created in the directory &lt;strong&gt;typo3conf/&lt;/strong&gt;.&lt;/p&gt;\n" +
                "&lt;p&gt;Just click the button below, the file will be created for you and the Install&#160;Tool will be unlocked.&lt;/p&gt;\n" +
                "&lt;p&gt;&lt;strong&gt;Security note: &lt;/strong&gt;When you are finished with your work, please log out of the Install&#160;Tool. Otherwise TYPO3 will automatically lock the Install&#160;Tool after one hour.&lt;/p&gt;\n" +
                "\t\t\t\t</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t\t<trans-unit id=\"confirmUnlockInstallToolButton\">\n" +
                "\t\t\t\t<source>Unlock the Install&amp;#160;Tool</source>\n" +
                "\t\t\t</trans-unit>\n" +
                "\t\t</body>\n" +
                "\t</file>\n" +
                "</xliff>\n");

        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, getProject());

        // may fail until we can register the xlf file extension conditionally
        assertContainsElements(allKeys, "LLL:EXT:baz/bar.xlf:mlang_labels_tabdescr");
        assertContainsElements(allKeys, "LLL:EXT:baz/bar.xml:.seeAlso");
    }
}
