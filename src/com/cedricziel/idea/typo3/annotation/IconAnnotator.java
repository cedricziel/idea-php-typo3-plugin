package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class IconAnnotator implements Annotator {
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

        PsiElement methodReference = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
        if (PhpElementsUtil.isMethodWithFirstStringOrFieldReference(methodReference, "getIcon")) {
            annotateIconUsage(psiElement, annotationHolder, value);
        }
    }

    private void annotateIconUsage(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        IconProvider iconProvider = IconProvider.getInstance(psiElement.getProject());
        annotateIcon(psiElement, annotationHolder, value, iconProvider);
    }

    private void annotateIcon(PsiElement psiElement, AnnotationHolder annotationHolder, String value, IconProvider iconProvider) {
        if (iconProvider.has(value)) {
            TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(), psiElement.getTextRange().getEndOffset());
            Annotation annotation = annotationHolder.createInfoAnnotation(range, null);
            annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
        } else {
            TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(), psiElement.getTextRange().getEndOffset());
            annotationHolder.createErrorAnnotation(range, "Unresolved icon");
        }
    }
}
