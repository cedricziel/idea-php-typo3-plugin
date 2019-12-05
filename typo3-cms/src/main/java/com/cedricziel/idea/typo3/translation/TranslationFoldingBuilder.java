package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TranslationFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        if (!TYPO3CMSProjectSettings.isEnabled(root) || !TYPO3CMSProjectSettings.getInstance(root.getProject()).translationEnableTextFolding) {
            return FoldingDescriptor.EMPTY;
        }

        FoldingGroup group = FoldingGroup.newGroup("TYPO3Translation");

        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<StringLiteralExpression> literalExpressions = PsiTreeUtil.findChildrenOfType(root, StringLiteralExpression.class);

        for (final StringLiteralExpression literalExpression : literalExpressions) {
            String value = literalExpression.getContents();

            if (value.startsWith("LLL:")) {
                final String transResult = Translator.translateLLLString(literalExpression);

                if (transResult != null) {
                    TextRange foldingRange = new TextRange(literalExpression.getTextRange().getStartOffset() + 1, literalExpression.getTextRange().getEndOffset() - 1);
                    descriptors.add(new FoldingDescriptor(literalExpression.getNode(), foldingRange, group) {
                        @Override
                        public String getPlaceholderText() {

                            return transResult;
                        }
                    });
                }
            }
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
