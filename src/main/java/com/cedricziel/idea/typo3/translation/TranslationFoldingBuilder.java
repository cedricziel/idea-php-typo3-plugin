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
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.psi.xml.XmlText;
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

                if (properties.size() == 1) {
                    TextRange foldingRange = new TextRange(literalExpression.getTextRange().getStartOffset() + 1, literalExpression.getTextRange().getEndOffset() - 1);
                    descriptors.add(new FoldingDescriptor(literalExpression.getNode(), foldingRange, group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {

                            PsiElement[] definitionElements = TranslationUtil.findDefinitionElements(project, value);
                            for (PsiElement definitionElement : definitionElements) {
                                if (definitionElement instanceof XmlTag) {
                                    for (XmlTag xmlTag : ((XmlTag) definitionElement).getSubTags()) {
                                        if (xmlTag.getName().equals("source")) {
                                            return xmlTag.getValue().getTrimmedText();
                                        }
                                    }
                                }
                            }
                            // IMPORTANT: keys can come with no values, so a test for null is needed
                            // IMPORTANT: Convert embedded \n to backslash n, so that the string will look like it has LF embedded
                            // in it and embedded " to escaped "
                            String valueOf = properties.get(0).getExtension();
                            return valueOf == null ? "" : valueOf.replaceAll("\n", "\\n").replaceAll("\"", "\\\\\"");
                        }
                    });
                }
            }
        }

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
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
