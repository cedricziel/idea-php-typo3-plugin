package com.cedricziel.idea.fluid.lang.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FluidRecursiveWalkingVisitor extends FluidVisitor implements PsiRecursiveVisitor {
    private final boolean myVisitAllFileRoots;
    private final PsiWalkingState myWalkingState = new PsiWalkingState(this) {
    };

    public FluidRecursiveWalkingVisitor() {
        this(false);
    }

    public FluidRecursiveWalkingVisitor(final boolean visitAllFileRoots) {
        myVisitAllFileRoots = visitAllFileRoots;
    }

    @Override
    public void visitElement(final @NotNull PsiElement element) {
        myWalkingState.elementStarted(element);
    }

    @Override
    public void visitFile(final @NotNull PsiFile file) {
        if (myVisitAllFileRoots) {
            final FileViewProvider viewProvider = file.getViewProvider();
            final List<PsiFile> allFiles = viewProvider.getAllFiles();
            if (allFiles.size() > 1) {
                if (file == viewProvider.getPsi(viewProvider.getBaseLanguage())) {
                    for (PsiFile lFile : allFiles) {
                        lFile.acceptChildren(this);
                    }
                    return;
                }
            }
        }

        super.visitFile(file);
    }

    public void stopWalking() {
        myWalkingState.stopWalking();
    }
}
