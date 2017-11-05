package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.extensionScanner.codeInspection.ClassConstantMatcherInspection;
import com.cedricziel.idea.typo3.extensionScanner.codeInspection.ClassNameMatcherInspection;
import com.cedricziel.idea.typo3.tca.codeInspection.InvalidQuantityInspection;
import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class TYPO3InspectionToolProvider implements InspectionToolProvider {
    @NotNull
    public Class[] getInspectionClasses() {

        return new Class[]{
                ExtbasePropertyInjectionInspection.class,
                MissingColumnTypeInspection.class,
                MissingRenderTypeInspection.class,
                MissingTableInspection.class,
                InvalidQuantityInspection.class,
                // Extension Scanner
                ClassConstantMatcherInspection.class,
                ClassNameMatcherInspection.class,
        };
    }
}
