package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class TranslationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {

        if (!(psiElement instanceof StringLiteralExpression)) {
            return;
        }

        StringLiteralExpression literalExpression = (StringLiteralExpression) psiElement;
        String value = literalExpression.getContents();

        if (value.isEmpty()) {
            return;
        }

        if (value.contains("LLL:")) {
            annotateTranslationUsage(psiElement, annotationHolder, value);
        }
    }

    private void annotateTranslationUsage(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        annotateTranslation(psiElement, annotationHolder, value);
    }

    private void annotateTranslation(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        if (FileBasedIndex.getInstance().getAllKeys(TranslationIndex.KEY, psiElement.getProject()).contains(value)) {
            annotationHolder.createInfoAnnotation(psiElement, null);
        } else {
            annotationHolder.createErrorAnnotation(psiElement, "Unresolved translation - this may occur if you defined the translation key only in TypoScript");
        }
    }
}
