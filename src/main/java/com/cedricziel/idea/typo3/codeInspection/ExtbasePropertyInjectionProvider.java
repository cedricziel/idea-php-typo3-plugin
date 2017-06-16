package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.InspectionToolProvider;

public class ExtbasePropertyInjectionProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{ExtbasePropertyInjectionInspection.class};
    }
}
