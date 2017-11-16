package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
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
        annotateIcon(psiElement, annotationHolder, value);
    }

    private void annotateIcon(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        if (IconIndex.getAllAvailableIcons(psiElement.getProject()).contains(value)) {
            annotationHolder.createInfoAnnotation(psiElement, null);
        } else {
            annotationHolder.createWarningAnnotation(psiElement, "Unresolved icon - this may also occur if the icon is defined in your extension, but not in the global icon registry.");
        }
    }
}
