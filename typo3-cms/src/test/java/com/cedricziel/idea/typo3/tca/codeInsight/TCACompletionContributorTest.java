package com.cedricziel.idea.typo3.tca.codeInsight;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.codeInsight.completion.CompletionType;
import com.jetbrains.php.lang.PhpFileType;

import java.util.Arrays;
import java.util.List;

public class TCACompletionContributorTest extends AbstractTestCase {

    public void testCanCompleteRenderTypes() {
        List<String> lookupElementStrings;

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['renderType' => '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("selectSingle"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['renderType' => 's<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("selectSingle"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['renderType'] = '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("selectSingle"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['renderType'] = 's<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("selectSingle"));
    }

    public void testCanCompleteTypes() {
        List<String> lookupElementStrings;

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['type' => '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("text"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['type' => 't<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("text"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['type'] = '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("text"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['type'] = 't<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("text"));
    }

    public void testCanCompleteTCAEvaluations() {
        List<String> lookupElementStrings;

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['eval' => '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("trim"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['eval' => 't<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("trim"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['eval' => 'alpha,<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete csv separated value", lookupElementStrings.contains("alpha,trim"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['eval'] = '<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.contains("trim"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['eval'] = 't<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("trim"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['eval'] = 'alpha,<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete csv separated value", lookupElementStrings.contains("alpha,trim"));
    }

    public void testCanCompleteTCAConfigSectionIndexes() {
        List<String> lookupElementStrings;

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['config'][\"<caret>\"] = '';");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.containsAll(Arrays.asList(TCAUtil.TCA_CONFIG_SECTION_CHILDREN)));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $GLOBALS['config']['r<caret>'] = '';");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("renderType"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['config' => ['<caret>' => '']];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.containsAll(Arrays.asList(TCAUtil.TCA_CONFIG_SECTION_CHILDREN)));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['config' => ['r<caret>' => ''];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("renderType"));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['config' => ['<caret>']];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete empty value", lookupElementStrings.containsAll(Arrays.asList(TCAUtil.TCA_CONFIG_SECTION_CHILDREN)));

        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $foo = ['config' => ['r<caret>'];");
        myFixture.complete(CompletionType.BASIC);
        lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue("Can complete partial value", lookupElementStrings.contains("renderType"));
    }
}
