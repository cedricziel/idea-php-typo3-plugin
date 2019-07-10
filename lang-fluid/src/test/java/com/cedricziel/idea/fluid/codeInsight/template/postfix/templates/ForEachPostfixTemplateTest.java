package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.template.impl.LiveTemplateCompletionContributor;

public class ForEachPostfixTemplateTest extends AbtractTemplateTest {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/codeInsight/template/postfix";
    }

    public void testCanExpandLiveTemplateOnVariable() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".for", "{foo.bar<caret>}", ForEachPostfixTemplate.class);
    }

    public void testCanExpandLiveTemplateOnChainedExpression() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".for", "{foo.bar -> my:view.helper()<caret>}", ForEachPostfixTemplate.class);
    }

    public void testForeachTemplate() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        doCompleteTest(".for", '\n');
    }
}
