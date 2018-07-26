package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.template.impl.LiveTemplateCompletionContributor;

public class ForEachPostfixTemplateTest extends AbtractTemplateTest {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/codeInsight/template/postfix";
    }

    public void testCanExpandLiveTemplateOnVariable() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".f", "{foo.bar<caret>}");
    }

    public void testCanExpandLiveTemplateOnChainedExpression() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".f", "{foo.bar -> my:view.helper()<caret>}");
    }

    public void testForeachTemplate() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        doCompleteTest(".f", '\n');
    }
}
