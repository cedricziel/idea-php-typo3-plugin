package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class TYPO3InspectionToolProvider implements InspectionToolProvider {
    @NotNull
    public Class[] getInspectionClasses() {

        return new Class[]{
                ExtbasePropertyInjectionInspection.class,
                MissingTableInspection.class,
        };
    }
}
