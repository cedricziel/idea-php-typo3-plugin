package com.cedricziel.idea.fluid.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class FluidConfigurationProvider extends ConfigurableProvider {

  private final Project myProject;

  public FluidConfigurationProvider(Project project) {
    myProject = project;
  }

  @Nullable
  @Override
  public Configurable createConfigurable() {
    return new FluidConfigurationPage(myProject);
  }

  @Override
  public boolean canCreateConfigurable() {
    return true;
  }
}
