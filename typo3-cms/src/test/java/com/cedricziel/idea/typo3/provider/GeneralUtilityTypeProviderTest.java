package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;

public class GeneralUtilityTypeProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/provider";
    }

    public void testCanResolveTypeFromMakeInstanceCall() {
        myFixture.copyFileToProject("classes.php");

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n" +
                "$instance = \\TYPO3\\CMS\\Core\\Utility\\GeneralUtility::makeInstance(\\App\\Apple::class);\n" +
                "$tree = $instance->tre<caret>e();",
            PlatformPatterns.psiElement(Method.class).withName("tree")
        );
    }
}
