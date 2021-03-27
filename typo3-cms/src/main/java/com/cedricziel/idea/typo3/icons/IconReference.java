package com.cedricziel.idea.typo3.icons;

import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.util.IconUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class IconReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String iconName;

    public IconReference(StringLiteralExpression psiElement) {
        super(psiElement);

        iconName = psiElement.getContents();
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(IconIndex.getIconDefinitionElements(myElement.getProject(), iconName));
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return IconUtil.createIconLookupElements(myElement.getProject());
    }
}
