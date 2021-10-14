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
                Method.class
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => 'coun<caret>t'];",
                UserFuncReference.class,
                Function.class
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => 'user_calc_my_stuf<caret>f'];",
                UserFuncReference.class,
                Function.class
        );

        assertResolvesTo(
                "bar.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>ux'];",
                UserFuncReference.class,
                Method.class
        );
    }

    public void testReferenceCanResolveVariants() {
        myFixture.copyFileToProject("classes.php");

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "quo",
                UserFuncReference.class
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "qux",
                UserFuncReference.class
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "quo",
                UserFuncReference.class
        );

        assertHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "qux",
                UserFuncReference.class
        );

        assertNotHasVariant(
                "foo.php",
                "<?php \n['userFunc' => 'Foo\\Bar->q<caret>'];",
                "invisible",
                UserFuncReference.class
        );

        assertNotHasVariant(
                "foo.php",
                "<?php \n['userFunc' => Foo\\Bar::class . '->q<caret>'];",
                "invisible",
                UserFuncReference.class
        );
    }
}
