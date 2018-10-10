package com.cedricziel.idea.typo3.annotation;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class IconAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!TYPO3CMSProjectSettings.getInstance(psiElement.getProject()).pluginEnabled) {
            return;
        }

        if (!TYPO3CMSProjectSettings.getInstance(psiElement.getProject()).iconAnnotatorEnabled) {
            return;
        }

        if (!stringParameterInMethodReference().accepts(psiElement)) {
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

    private PsiElementPattern.Capture<StringLiteralExpression> stringParameterInMethodReference() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(ParameterList.class);
    }

    private void annotateIconUsage(PsiElement psiElement, AnnotationHolder annotationHolder, String value) {
        if (IconIndex.getAllAvailableIcons(psiElement.getProject()).contains(value)) {
            return;
        }

        if (IconIndex.getDeprecatedIconIdentifiers(psiElement.getProject()).containsKey(value)) {
            return;
        }

        annotationHolder.createWarningAnnotation(new TextRange(psiElement.getTextRange().getStartOffset() + 1, psiElement.getTextRange().getEndOffset() - 1), "Unresolved icon - this may also occur if the icon is defined in your extension, but not in the global icon registry.");
    }
}
