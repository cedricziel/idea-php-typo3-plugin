package com.cedricziel.idea.typo3.util;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.MethodReference;

public class PhpTypeProviderUtilTest extends AbstractTestCase {
    public void testSignaturesAreProvided() {
        MethodReference methodReference = PhpPsiElementFactory.createMethodReference(
            getProject(),
            "<?php\n" +
                "use App\\Foo\\Bar\\Foo;\n" +
                "GeneralUtility::makeInstance(Foo::class);"
        );
        String referenceSignatureByFirstParameter = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(methodReference, '%');

        assertEquals("#M#C\\GeneralUtility.makeInstance%#K#C\\App\\Foo\\Bar\\Foo.class", referenceSignatureByFirstParameter);
    }
}
