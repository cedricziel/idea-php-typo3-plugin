package com.cedricziel.idea.typo3.translation;

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
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
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
        FoldingGroup group = FoldingGroup.newGroup("TYPO3Translation");

        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<StringLiteralExpression> literalExpressions = PsiTreeUtil.findChildrenOfType(root, StringLiteralExpression.class);

        for (final StringLiteralExpression literalExpression : literalExpressions) {
            String value = literalExpression.getContents();

            if (value.startsWith("LLL:")) {
                Project project = literalExpression.getProject();

                final List<StubTranslation> properties = TranslationIndex.findById(project, value);

                StubTranslation defaultTranslation = findDefaultTranslationFromVariants(properties);

                if (defaultTranslation != null) {
                    TextRange foldingRange = new TextRange(literalExpression.getTextRange().getStartOffset() + 1, literalExpression.getTextRange().getEndOffset() - 1);
                    descriptors.add(new FoldingDescriptor(literalExpression.getNode(), foldingRange, group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {

                            PsiElement[] definitionElements = TranslationUtil.findDefinitionElements(project, value);
                            for (PsiElement definitionElement : definitionElements) {
                                if (definitionElement instanceof XmlTag) {
                                    if (((XmlTag) definitionElement).getName().equals("label")) {
                                        return ((XmlTag) definitionElement).getValue().getTrimmedText();
                                    }

                                    for (XmlTag xmlTag : ((XmlTag) definitionElement).getSubTags()) {
                                        if (xmlTag.getName().equals("source")) {
                                            return xmlTag.getValue().getTrimmedText();
                                        }
                                    }
                                }

                                if (definitionElement instanceof XmlAttributeValue) {
                                    if (((XmlTag) definitionElement.getParent().getParent()).getName().equals("label")) {
                                        return ((XmlTag) definitionElement).getValue().getTrimmedText();
                                    }

                                    for (XmlTag xmlTag : ((XmlTag) definitionElement.getParent().getParent()).getSubTags()) {
                                        if (xmlTag.getName().equals("source")) {
                                            return xmlTag.getValue().getTrimmedText();
                                        }
                                    }
                                }
                            }

                            return null;
                        }
                    });
                }
            }
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private StubTranslation findDefaultTranslationFromVariants(List<StubTranslation> properties) {
        if (properties.size() == 1) {
            return properties.iterator().next();
        }

        if (properties.size() > 1) {
            for (StubTranslation property : properties) {
                // TYPO3 CMS documentation suggests that every element has a mandatory english variant
                if (property.getLanguage().equals("en")) {
                    return property;
                }
            }

            // default if no english element was found
            return properties.iterator().next();
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
