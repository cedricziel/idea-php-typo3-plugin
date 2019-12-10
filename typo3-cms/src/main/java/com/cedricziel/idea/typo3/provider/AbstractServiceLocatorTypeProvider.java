package com.cedricziel.idea.typo3.provider;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

abstract public class AbstractServiceLocatorTypeProvider implements PhpTypeProvider4 {

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        int endIndex = expression.lastIndexOf("%");
        if (endIndex == -1) {
            return Collections.emptySet();
        }

        // Get FQN from parameter string.
        // Example (PhpStorm 8): #K#C\Foo\Bar::get()%#K#C\Bar\Baz. -> \Bar\Baz.
        // Example (PhpStorm 9): #K#C\Foo\Bar::get()%#K#C\Bar\Baz.class -> \Bar\Baz.class
        String parameter = expression.substring(endIndex + 5);

        if (parameter.contains(".class")) { // for PhpStorm 9
            parameter = parameter.replace(".class", "");
        }

        if (parameter.contains(".")) {
            parameter = parameter.replace(".", "");
        }

        return PhpIndex.getInstance(project).getAnyByFQN(parameter);
    }
}
