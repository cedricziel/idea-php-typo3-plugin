package com.cedricziel.idea.typo3.userFunc;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;

public class UserFuncReferenceTest extends AbstractTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/userFunc";
    }

    public void testReferenceCanResolveDefinition() {
        myFixture.copyFileToProject("classes.php");

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>ux'];",
                UserFuncReference.class,
                Method.class,
                "A class method can be resolved"
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => 'coun<caret>t'];",
                UserFuncReference.class,
                Function.class,
                "A global function can be resolved"
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => 'user_calc_my_stuf<caret>f'];",
                UserFuncReference.class,
                Function.class,
                "A global function can be resolved"
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>ux'];",
                UserFuncReference.class,
                Method.class,
                "A class method can be resolved through concatenation"
        );
    }

    public void testReferenceCanResolveVariants() {
        myFixture.copyFileToProject("classes.php");

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "quo",
                UserFuncReference.class,
                "Reference can correctly resolve public methods as variants"
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "qux",
                UserFuncReference.class,
                "Reference can correctly resolve static methods as variants"
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "quo",
                UserFuncReference.class,
                "Reference can correctly resolve public methods as variants on concatenation expression"
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "qux",
                UserFuncReference.class,
                "Reference can correctly resolve static methods as variants on concatenation expression"
        );

        assertNotHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "invisible",
                UserFuncReference.class,
                "Reference does not present inaccessible variants (non public methods)"
        );

        assertNotHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "invisible",
                UserFuncReference.class,
                "Reference does not present inaccessible variants (non public methods) on concatenation expression"
        );
    }
}
