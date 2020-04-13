package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.cedricziel.idea.typo3.codeInspection.PluginEnabledPhpInspection;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ConstantReference;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstantMatcherInspection extends PluginEnabledPhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Constant removed with TYPO3 9";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildRealVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                if (!PlatformPatterns.psiElement(PhpElementTypes.CONSTANT_REF).accepts(element)) {
                    return;
                }

                ConstantReference constantReference = (ConstantReference) element;
                Set<String> constants = getRemovedConstantsFQNs(constantReference);
                if (constants.contains(constantReference.getFQN())) {
                    problemsHolder.registerProblem(element, "Constant removed with TYPO3 9, consider using an alternative");
                }
            }
        };
    }

    private Set<String> getRemovedConstantsFQNs(ConstantReference element) {
        Set<PsiElement> elements = new HashSet<>();
        PsiFile[] constantMatcherFiles = FilenameIndex.getFilesByName(element.getProject(), "ConstantMatcher.php", GlobalSearchScope.allScope(element.getProject()));
        for (PsiFile file : constantMatcherFiles) {

            Collections.addAll(
                elements,
                PsiTreeUtil.collectElements(file, el -> PlatformPatterns
                    .psiElement(StringLiteralExpression.class)
                    .withParent(
                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                            .withAncestor(
                                4,
                                PlatformPatterns.psiElement(PhpElementTypes.RETURN)
                            )
                    )
                    .accepts(el)
                )
            );
        }

        return elements.stream()
            .map(stringLiteral -> "\\" + ((StringLiteralExpression) stringLiteral).getContents())
            .collect(Collectors.toSet());
    }
}
