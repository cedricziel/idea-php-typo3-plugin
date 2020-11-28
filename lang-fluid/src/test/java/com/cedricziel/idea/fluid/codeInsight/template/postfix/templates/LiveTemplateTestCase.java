package com.cedricziel.idea.fluid.codeInsight.template.postfix.templates;

import com.cedricziel.idea.fluid.FluidCompletionAutoPopupTestCase;
import com.intellij.testFramework.EdtTestUtil;

abstract public class LiveTemplateTestCase extends FluidCompletionAutoPopupTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/fluid/codeInsight/template/postfix";
    }

    protected void doTest() {
        configureByFile();
        myFixture.type("\t");
        checkResultByFile();
    }

    private void configureByFile() {
        EdtTestUtil.runInEdtAndWait(() -> myFixture.configureByFile(getTestName(true) + ".fluid"));
    }

    private void checkResultByFile() {
        myFixture.checkResultByFile(getTestName(true) + "_after.fluid", true);
    }
}
