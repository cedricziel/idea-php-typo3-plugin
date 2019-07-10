package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.template.impl.LiveTemplateCompletionContributor;

public class DebugInlinePostfixTemplateTest extends AbtractTemplateTest {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/codeInsight/template/postfix";
    }

    public void testCanExpandLiveTemplateOnVariable() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".deb", "{foo.bar<caret>}", DebugInlinePostfixTemplate.class);
    }

    public void testCanExpandLiveTemplateOnChainedExpression() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".deb", "{foo.bar -> my:view.helper()<caret>}", DebugInlinePostfixTemplate.class);
    }

    public void testDebugTemplate() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        doCompleteTest(".deb", '\n');
    }
}
