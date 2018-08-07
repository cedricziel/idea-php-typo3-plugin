package com.cedricziel.idea.fluid.variables;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.cedricziel.idea.fluid.util.FluidTypeResolver.getClassFromPhpTypeSet;

public class FluidTypeContainer {

    private PhpNamedElement phpNamedElement;
    private String stringElement;
    private Object dataHolder;

    public FluidTypeContainer(PhpNamedElement phpNamedElement) {
        this.phpNamedElement = phpNamedElement;
    }

    public FluidTypeContainer(String stringElement) {
        this.stringElement = stringElement;
    }

    public static Collection<FluidTypeContainer> fromCollection(Project project, Collection<FluidVariable> fluidVariables) {

        List<FluidTypeContainer> fluidTypeContainerList = new ArrayList<>();
        for (FluidVariable variable : fluidVariables) {
            Collection<PhpClass> phpClass = getClassFromPhpTypeSet(project, variable.getTypes());
            if (phpClass.size() > 0) {
                fluidTypeContainerList.add(new FluidTypeContainer(phpClass.iterator().next()));
            }
        }

        return fluidTypeContainerList;
    }

    @Nullable
    public PhpNamedElement getPhpNamedElement() {
        return phpNamedElement;
    }

    @Nullable
    public String getStringElement() {
        return stringElement;
    }

    public FluidTypeContainer withDataHolder(Object object) {
        this.dataHolder = object;
        return this;
    }

    public Object getDataHolder() {
        return dataHolder;
    }
}
