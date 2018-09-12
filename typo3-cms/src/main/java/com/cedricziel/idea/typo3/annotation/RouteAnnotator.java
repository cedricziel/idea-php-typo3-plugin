package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.RouteIndex;
import com.cedricziel.idea.typo3.routing.RouteReference;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RouteAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!TYPO3CMSProjectSettings.getInstance(psiElement.getProject()).pluginEnabled) {
            return;
        }

        if (!TYPO3CMSProjectSettings.getInstance(psiElement.getProject()).routeAnnotatorEnabled) {
            return;
        }

        if (!(psiElement instanceof StringLiteralExpression)) {
            return;
        }

        StringLiteralExpression literalExpression = (StringLiteralExpression) psiElement;
        String value = literalExpression.getContents();

        if (value.isEmpty()) {
            return;
        }

        for (PsiReference psiReference : literalExpression.getReferences()) {
            if (psiReference instanceof RouteReference) {
                annotateRoute(psiElement, annotationHolder, value);
            }
        }
    }

    private void annotateRoute(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder, @NotNull String value) {
        Collection<String> allKeys = FileBasedIndex.getInstance().getAllKeys(RouteIndex.KEY, psiElement.getProject());

        if (allKeys.contains(value)) {
            TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(), psiElement.getTextRange().getEndOffset());
            Annotation annotation = annotationHolder.createInfoAnnotation(range, null);
            annotation.setTextAttributes(DefaultLanguageHighlighterColors.STRING);
        } else {
            TextRange range = new TextRange(psiElement.getTextRange().getStartOffset() + 1, psiElement.getTextRange().getEndOffset() - 1);
            annotationHolder.createWeakWarningAnnotation(range, "Unresolved route");
        }
    }
}
