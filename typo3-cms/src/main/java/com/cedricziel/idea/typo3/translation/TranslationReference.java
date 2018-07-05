package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class TranslationReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String translationId;

    public TranslationReference(StringLiteralExpression psiElement) {
        super(psiElement);

        this.translationId = psiElement.getContents();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(TranslationUtil.findDefinitionElements(myElement.getProject(), translationId));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return TranslationUtil.createLookupElements(myElement.getProject());
    }
}
