package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.completion.CompletionAutoPopupTestCase;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.codeInsight.template.impl.LiveTemplateCompletionContributor;
import com.intellij.codeInsight.template.postfix.completion.PostfixTemplateLookupElement;
import com.intellij.codeInsight.template.postfix.settings.PostfixTemplatesSettings;
import com.intellij.testFramework.EdtTestUtil;

import java.util.HashMap;

abstract public class AbtractTemplateTest extends CompletionAutoPopupTestCase {
    @Override
    public void setUp() throws Exception {

        super.setUp();

        LiveTemplateCompletionContributor.setShowTemplatesInTests(false, myFixture.getTestRootDisposable());
    }

    @Override
    public void tearDown() throws Exception {
        try {
            PostfixTemplatesSettings settings = PostfixTemplatesSettings.getInstance();
            assertNotNull(settings);
            settings.setProviderToDisabledTemplates(new HashMap<>());
            settings.setPostfixTemplatesEnabled(true);
            settings.setTemplatesCompletionEnabled(true);
        } finally {
            super.tearDown();
        }
    }

    protected void doCompleteTest(String textToType, char c) {
        configureByFile();
        type(textToType);
        assertNotNull(getLookup());
        myFixture.type(c);
        checkResultByFile();
    }

    private void configureByFile() {
        EdtTestUtil.runInEdtAndWait(() -> myFixture.configureByFile(getTestName(true) + ".fluid"));
    }

    private void checkResultByFile() {
        myFixture.checkResultByFile(getTestName(true) + "_after.fluid", true);
    }

    protected void testLiveTemplateIsAvailable(String completionChar, String templateContent, Class templateClass) {
        myFixture.configureByText("foo.fluid", templateContent);
        type(completionChar);
        LookupImpl lookup = getLookup();
        assertNotNull(lookup);

        LookupElement item = lookup.getCurrentItem();
        assertNotNull(item);
        assertInstanceOf(item, PostfixTemplateLookupElement.class);
        assertInstanceOf(((PostfixTemplateLookupElement) item).getPostfixTemplate(), templateClass);
    }
}
