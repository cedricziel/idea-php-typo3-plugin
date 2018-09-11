package com.cedricziel.idea.typo3.codeInspection;

import com.cedricziel.idea.typo3.extensionScanner.codeInspection.*;
import com.cedricziel.idea.typo3.tca.codeInspection.InvalidQuantityInspection;
import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class TYPO3InspectionToolProvider implements InspectionToolProvider {
    @NotNull
    public Class[] getInspectionClasses() {

        return new Class[]{
                MissingTableInspection.class,
                InvalidQuantityInspection.class,
                // Code Migration
                LegacyClassesForIDEInspection.class,
                // Extension Scanner
                ClassConstantMatcherInspection.class,
                ClassNameMatcherInspection.class,
                ConstantMatcherInspection.class,
                FunctionCallMatcherInspection.class,
                MethodArgumentDroppedMatcherInspection.class,
        };
    }
}
