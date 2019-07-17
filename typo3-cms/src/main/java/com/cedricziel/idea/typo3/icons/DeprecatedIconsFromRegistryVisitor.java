package com.cedricziel.idea.typo3.icons;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;

import java.util.HashMap;
import java.util.Map;

public class DeprecatedIconsFromRegistryVisitor extends PhpRecursiveElementVisitor {
    public Map<String, String> result;

    public DeprecatedIconsFromRegistryVisitor() {
        this.result = new HashMap<>();
    }

    @Override
    public void visitElement(PsiElement element) {
        if (deprecatedIconArrayKeyPattern().accepts(element)) {
            Field field = (Field) PsiTreeUtil.findFirstParent(element, e -> e instanceof Field);
            if (field == null || !field.getName().equals("deprecatedIcons")) {
                super.visitElement(element);
                return;
            }

            String iconIdentifier = "";
            String iconReplacement= "";
            StringLiteralExpression literalExpression = (StringLiteralExpression) element;
            iconIdentifier = literalExpression.getContents();

            // pre-9, the the deprecatedIcons field was a multi-dimensional array
            ArrayHashElement hashElement = (ArrayHashElement) PsiTreeUtil.findFirstParent(element, e -> e instanceof ArrayHashElement);
            if (hashElement != null && hashElement.getValue() instanceof ArrayCreationExpression) {
                Iterable<ArrayHashElement> hashElements = ((ArrayCreationExpression) hashElement.getValue()).getHashElements();
                for (ArrayHashElement arrayHashElement : hashElements) {
                    if (arrayHashElement.getKey() instanceof StringLiteralExpression && ((StringLiteralExpression) arrayHashElement.getKey()).getContents().equals("replacement")) {
                        if (arrayHashElement.getValue() instanceof StringLiteralExpression) {
                            result.putIfAbsent(iconIdentifier, ((StringLiteralExpression) arrayHashElement.getValue()).getContents());
                        }
                    }
                }
            } else if (hashElement != null && hashElement.getValue() instanceof StringLiteralExpression) {
                result.putIfAbsent(iconIdentifier, ((StringLiteralExpression) hashElement.getValue()).getContents());
            } else {
                result.putIfAbsent(iconIdentifier, iconReplacement);
            }
        }

        super.visitElement(element);
    }

    private PsiElementPattern.Capture<StringLiteralExpression> deprecatedIconArrayKeyPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
            PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withSuperParent(3, Field.class)
        );
    }
}
