package com.cedricziel.idea.typo3.icons;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
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
            if (field != null && field.getName().equals("deprecatedIcons")) {
                String iconIdentifier = "";
                StringLiteralExpression literalExpression = (StringLiteralExpression) element;
                iconIdentifier = literalExpression.getContents();


                result.putIfAbsent(iconIdentifier, "");
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
