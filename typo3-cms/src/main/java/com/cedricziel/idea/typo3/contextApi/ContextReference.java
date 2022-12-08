package com.cedricziel.idea.typo3.contextApi;

import com.cedricziel.idea.typo3.util.TYPO3Utility;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContextReference extends PsiPolyVariantReferenceBase<PsiElement> {
    public ContextReference(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myElement instanceof StringLiteralExpression) {
            String aspectFQN = TYPO3Utility.getFQNByAspectName(((StringLiteralExpression) myElement).getContents());
            if (aspectFQN == null) {
                return ResolveResult.EMPTY_ARRAY;
            }

            return PsiElementResolveResult.createResults(PhpIndex.getInstance(myElement.getProject()).getClassesByFQN(aspectFQN));
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        List<LookupElement> elements = new ArrayList<>();
        for (String availableAspect : TYPO3Utility.getAvailableAspects()) {
            elements.add(
                LookupElementBuilder
                    .create(availableAspect)
                    .withIcon(PhpIcons.CLASS)
                    .withTypeText(TYPO3Utility.getFQNByAspectName(availableAspect), true)
            );
        }

        return elements.toArray();
    }
}
