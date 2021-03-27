package com.cedricziel.idea.typo3.resources;

import com.cedricziel.idea.typo3.util.ResourceUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLQuotedText;

public class ResourceReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String resourceName;

    public ResourceReference(StringLiteralExpression psiElement) {
        super(psiElement);

        this.resourceName = psiElement.getContents();
    }

    public ResourceReference(YAMLQuotedText psiElement) {
        super(psiElement);

        this.resourceName = psiElement.getTextValue();
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(ResourceUtil.getResourceDefinitionElements(myElement.getProject(), resourceName));
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return ResourceUtil.getResourceLookupElements(myElement.getProject());
    }
}
