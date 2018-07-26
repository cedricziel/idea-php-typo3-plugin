package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.intellij.codeInsight.template.impl.LiveTemplateCompletionContributor;

public class AliasPostfixTemplateTest extends AbtractTemplateTest {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/codeInsight/template/postfix";
    }

    public void testCanExpandLiveTemplateOnVariable() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".alias", "{foo.bar<caret>}", AliasPostfixTemplate.class);
    }

    public void testCanExpandLiveTemplateOnChainedExpression() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        testLiveTemplateIsAvailable(".alias", "{foo.bar -> my:view.helper()<caret>}", AliasPostfixTemplate.class);
    }

    public void testAliasTemplate() {
        LiveTemplateCompletionContributor.setShowTemplatesInTests(true, myFixture.getTestRootDisposable());

        doCompleteTest(".alias", '\n');
    }
}
