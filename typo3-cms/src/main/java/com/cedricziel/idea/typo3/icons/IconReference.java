package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.util.IconUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class IconReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String iconIdentifier;

    public IconReference(StringLiteralExpression psiElement) {
        super(psiElement);

        iconIdentifier = psiElement.getContents();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(
            IconIndex.getIcon(myElement.getProject(), iconIdentifier).stream().map(IconStub::getElement).collect(Collectors.toList())
        );
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return IconUtil.createIconLookupElements(myElement.getProject());
    }
}
