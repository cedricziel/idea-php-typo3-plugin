package com.cedricziel.idea.typo3.dispatcher;

import com.cedricziel.idea.typo3.util.SignalSlotDispatcherUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignalSlotMethodReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String methodName;
    private final String classFqn;

    public SignalSlotMethodReference(@NotNull ClassConstantReference classConstantReference, @NotNull StringLiteralExpression subject) {
        super(subject);

        this.methodName = subject.getContents();

        ClassReference classReference = PsiTreeUtil.findChildOfType(classConstantReference, ClassReference.class);
        if (classReference == null || classReference.getFQN() == null) {
            classFqn = "";
            subject.getContents();

            return;
        }

        this.classFqn = classReference.getFQN();
    }

    public SignalSlotMethodReference(@NotNull ClassConstantReference classConstantReference, @NotNull StringLiteralExpression subject, TextRange range) {
        super(subject, range);

        this.methodName = subject.getContents();

        ClassReference classReference = PsiTreeUtil.findChildOfType(classConstantReference, ClassReference.class);
        if (classReference == null || classReference.getFQN() == null) {
            classFqn = "";

            return;
        }

        this.classFqn = classReference.getFQN();
    }

    public SignalSlotMethodReference(@NotNull StringLiteralExpression className, @NotNull StringLiteralExpression subject) {
        super(subject);

        this.classFqn = className.getContents();
        this.methodName = subject.getContents();
    }

    public SignalSlotMethodReference(@NotNull StringLiteralExpression className, @NotNull StringLiteralExpression subject, TextRange range) {
        super(subject, range);

        this.classFqn = className.getContents();
        this.methodName = subject.getContents();
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (classFqn.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY;
        }

        List<ResolveResult> results = new ArrayList<>();
        PsiElement[] psiElements = SignalSlotDispatcherUtil.getSignalPsiElements(myElement.getProject(), classFqn, methodName);

        for (PsiElement psiElement : psiElements) {
            results.add(new PsiElementResolveResult(psiElement));
        }

        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        if (classFqn.isEmpty()) {
            return LookupElement.EMPTY_ARRAY;
        }

        return SignalSlotDispatcherUtil.getPossibleSlotMethodLookupElements(myElement.getProject(), classFqn);
    }
}
