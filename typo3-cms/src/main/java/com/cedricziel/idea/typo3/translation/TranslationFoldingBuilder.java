package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.Project;
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
                Project project = literalExpression.getProject();

                final List<StubTranslation> properties = TranslationIndex.findById(project, value);

                StubTranslation defaultTranslation = findDefaultTranslationFromVariants(project, properties);

                if (defaultTranslation != null) {
                    TextRange foldingRange = new TextRange(literalExpression.getTextRange().getStartOffset() + 1, literalExpression.getTextRange().getEndOffset() - 1);
                    descriptors.add(new FoldingDescriptor(literalExpression.getNode(), foldingRange, group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {

                            return TranslationUtil.findPlaceholderTextFor(project, defaultTranslation);
                        }
                    });
                }
            }
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private StubTranslation findDefaultTranslationFromVariants(Project project, List<StubTranslation> stubs) {
        if (stubs.size() == 1) {
            return stubs.iterator().next();
        }

        // Try to find the one with the selected favorite locale
        String defaultLocale = (String) TYPO3CMSProjectSettings.getInstance(project).translationFavoriteLocale;
        if (defaultLocale != null && !defaultLocale.isEmpty()) {
            for (StubTranslation property : stubs) {
                if (property.getLanguage().equals(defaultLocale)) {
                    return property;
                }
            }
        }

        // if default locale is not matched, try to find the "default" locale
        for (StubTranslation property : stubs) {
            if (property.getLanguage().equals("default")) {
                return property;
            }
        }

        if (stubs.size() > 1) {
            for (StubTranslation property : stubs) {
                // TYPO3 CMS documentation suggests that every element has a mandatory english variant
                if (property.getLanguage().equals("en")) {
                    return property;
                }
            }

            // default if no english element was found
            return stubs.iterator().next();
        }

        return null;
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
