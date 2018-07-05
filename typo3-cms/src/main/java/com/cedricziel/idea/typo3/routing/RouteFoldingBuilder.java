package com.cedricziel.idea.typo3.routing;

import com.cedricziel.idea.typo3.index.RouteIndex;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("TYPO3Route");

        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<StringLiteralExpression> literalExpressions = PsiTreeUtil.findChildrenOfType(root, StringLiteralExpression.class);

        for (final StringLiteralExpression literalExpression : literalExpressions) {
            for (PsiReference reference : literalExpression.getReferences()) {
                if (reference instanceof RouteReference) {
                    String value = literalExpression.getContents();

                    FoldingDescriptor descriptor = foldRouteReferenceString(reference, value, group);
                    if (descriptor != null) {
                        descriptors.add(descriptor);
                    }
                }
            }


        }

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    private FoldingDescriptor foldRouteReferenceString(PsiReference reference, String value, FoldingGroup group) {
        PsiElement element = reference.getElement();
        TextRange foldingRange = new TextRange(element.getTextRange().getStartOffset() + 1, element.getTextRange().getEndOffset() - 1);

        if (!RouteIndex.hasRoute(element.getProject(), value)) {
            return null;
        }

        Collection<RouteStub> route = RouteIndex.getRoute(element.getProject(), value);
        if (route.size() == 0) {
            return null;
        }

        RouteStub routeDef = route.iterator().next();

        return new FoldingDescriptor(element.getNode(), foldingRange, group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                if (routeDef.getPath() == null) {
                    return routeDef.getController() + "::" + routeDef.getMethod();
                }

                return routeDef.getPath();
            }
        };
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
