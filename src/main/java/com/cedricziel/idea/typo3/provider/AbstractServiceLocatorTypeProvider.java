package com.cedricziel.idea.typo3.provider;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;

import java.util.Collection;
import java.util.Collections;

abstract public class AbstractServiceLocatorTypeProvider implements PhpTypeProvider2 {
    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        int endIndex = s.lastIndexOf("%");
        if (endIndex == -1) {
            return Collections.emptySet();
        }

        // Get FQN from parameter string.
        // Example (PhpStorm 8): #K#C\Foo\Bar::get()%#K#C\Bar\Baz. -> \Bar\Baz.
        // Example (PhpStorm 9): #K#C\Foo\Bar::get()%#K#C\Bar\Baz.class -> \Bar\Baz.class
        String parameter = s.substring(endIndex + 5, s.length());

        if (parameter.contains(".class")) { // for PhpStorm 9
            parameter = parameter.replace(".class", "");
        }

        if (parameter.contains(".")) {
            parameter = parameter.replace(".", "");
        }

        return PhpIndex.getInstance(project).getAnyByFQN(parameter);
    }
}
