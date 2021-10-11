package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.util.indexing.FileBasedIndex;

abstract public class AbstractTranslationTest extends AbstractTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> FileTypeManager.getInstance().associateExtension(XmlFileType.INSTANCE, "xlf"));

        FileBasedIndex.getInstance().requestRebuild(TranslationIndex.KEY);

        myFixture.addFileToProject("typo3conf/ext/foo/ext_emconf.php", "");

        myFixture.copyFileToProject("sample.xlf", "typo3conf/ext/foo/sample.xlf");
        myFixture.copyFileToProject("de.sample.xlf", "typo3conf/ext/foo/de.sample.xlf");
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/index/translation";
    }
}
