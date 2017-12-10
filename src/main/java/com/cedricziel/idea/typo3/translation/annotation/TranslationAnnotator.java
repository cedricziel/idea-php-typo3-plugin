package com.cedricziel.idea.typo3.translation.annotation;

import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression;
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

        if (TranslationUtil.isTranslationKeyString(value) && !(psiElement.getParent() instanceof ConcatenationExpression)) {
            annotateTranslationUsage(psiElement, annotationHolder, value);
        }
    }

    private void annotateTranslationUsage(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        annotateTranslation(psiElement, annotationHolder, value);
    }

    private void annotateTranslation(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        if (TranslationUtil.keyExists(psiElement.getProject(), value)) {
            annotationHolder.createInfoAnnotation(psiElement, null);
        } else {
            annotationHolder.createErrorAnnotation(psiElement, "Unresolved translation - this may occur if you defined the translation key only in TypoScript");
        }
    }
}
