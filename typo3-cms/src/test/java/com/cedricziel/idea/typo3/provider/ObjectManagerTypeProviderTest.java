package com.cedricziel.idea.typo3.provider;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;

public class ObjectManagerTypeProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/provider";
    }

    public void testCanResolveTypeFromMakeInstanceCall() {
        myFixture.copyFileToProject("classes.php");

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var $objectManager \\TYPO3\\CMS\\Extbase\\Object\\ObjectManagerInterface */\n" +
                "$instance = $objectManager->get(\\App\\Apple::class);\n" +
                "$tree = $instance->tre<caret>e();",
            PlatformPatterns.psiElement(Method.class).withName("tree")
        );
    }

    public void testIssue305() {
        myFixture.copyFileToProject("classes.php");

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var $objectManager \\TYPO3\\CMS\\Extbase\\Object\\ObjectManagerInterface */\n" +
                "$dataMapper = $objectManager->get(\\TYPO3\\CMS\\Extbase\\Persistence\\Generic\\Mapper\\DataMapper::class);\n" +
                "$dataMapper->ma<caret>p();",
            PlatformPatterns.psiElement(Method.class).withName("map")
        );
    }

    public void testIssue305_class() {
        myFixture.copyFileToProject("classes.php");

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n" +
                "class Foo {\n" +
                "  /** @var \\TYPO3\\CMS\\Extbase\\Object\\ObjectManagerInterface */" +
                "  protected $objectManager;\n" +
                "  public function foo() {\n" +
                "    $dataMapper = $this->objectManager->get(\\TYPO3\\CMS\\Extbase\\Persistence\\Generic\\Mapper\\DataMapper::class);\n" +
                "    $dataMapper->ma<caret>p();" +
                "  }\n" +
                "}",
            PlatformPatterns.psiElement(Method.class).withName("map")
        );
    }
}
