package com.cedricziel.idea.fluid;

import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

abstract public class AbstractFluidTest extends LightCodeInsightFixtureTestCase {
    protected static void assertContainsNamespace(List<FluidNamespace> namespaces, String prefix, String namespace) {
        for (FluidNamespace fluidNamespace : namespaces) {
            if (fluidNamespace.prefix.equals(prefix) && fluidNamespace.namespace.equals(namespace)) {
                return;
            }
        }

        fail(String.format("Expected %s bound to %s, but didnt find it.", namespace, prefix));
    }
}
