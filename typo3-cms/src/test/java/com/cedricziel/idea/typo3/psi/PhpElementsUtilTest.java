package com.cedricziel.idea.typo3.psi;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public class PhpElementsUtilTest extends BasePlatformTestCase {
    public void testCanDetectSuperClass() {
        myFixture.addFileToProject("A.php", "<?php\n" +
                "namespace Foo;\n" +
                "class A {}\n" +
                "class B extends A {}\n" +
                "class C extends B {}");

        PhpClass classA = PhpIndex.getInstance(myFixture.getProject()).getClassesByFQN("\\Foo\\A").iterator().next();
        assertNotNull("Class A can be found", classA);
        PhpClass classB = PhpIndex.getInstance(myFixture.getProject()).getClassesByFQN("\\Foo\\B").iterator().next();
        assertNotNull("Class B can be found", classB);
        PhpClass classC = PhpIndex.getInstance(myFixture.getProject()).getClassesByFQN("\\Foo\\C").iterator().next();
        assertNotNull("Class C can be found", classC);

        assertTrue("Superclasses can be found", PhpElementsUtil.hasSuperClass(classC, "\\Foo\\A"));
        assertFalse("Superclasses can not found", PhpElementsUtil.hasSuperClass(classA, "\\Foo\\C"));
    }
}
