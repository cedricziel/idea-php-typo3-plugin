package com.cedricziel.idea.fluid.lang.psi;

import com.cedricziel.idea.fluid.lang.FluidFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

public class FluidElementFactory {
  public static FluidFile createFile(@NotNull Project project, @NotNull String text) {
    String name = "dummy.fluid";

    return (FluidFile) PsiFileFactory.getInstance(project).createFileFromText(name, FluidFileType.INSTANCE, text);
  }
}
