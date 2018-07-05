package com.cedricziel.idea.fluid.lang.psi;

import com.cedricziel.idea.fluid.lang.FluidFileType;
import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FluidFile extends PsiFileBase {
    public FluidFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FluidLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return FluidFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Fluid File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
