package com.cedricziel.idea.fluid;

import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.CompletionAutoPopupTester;

abstract public class FluidCompletionAutoPopupTestCase extends BasePlatformTestCase {
    protected CompletionAutoPopupTester myTester;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myTester = new CompletionAutoPopupTester(myFixture);
    }

    public void type(String s) {
        myTester.typeWithPauses(s);
    }

    @Override
    protected boolean runInDispatchThread() {
        return false;
    }

    public LookupImpl getLookup() {
        return (LookupImpl)myFixture.getLookup();
    }
}
