package com.cedricziel.idea.typo3.extensionScanner.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassConstantMatcherInspection extends PhpInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getDisplayName() {
        return "Deprecated class constant";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {

                if (!PlatformPatterns.psiElement(PhpElementTypes.CLASS_CONSTANT_REFERENCE).accepts(element)) {
                    return;
                }

                Set<String> constants = getDeprecatedClassConstants(element);
                ClassConstantReference classConstantReference = (ClassConstantReference) element;
                if (constants.contains(classConstantReference.getText())) {
                    problemsHolder.registerProblem(element, "Deprecated class constant");
                }
            }
        };
    }

    private Set<String> getDeprecatedClassConstants(PhpPsiElement element) {
        Set<PsiElement> elements = new HashSet<>();
        PsiFile[] classConstantMatcherFiles = FilenameIndex.getFilesByName(element.getProject(), "ClassConstantMatcher.php", GlobalSearchScope.allScope(element.getProject()));
        for (PsiFile file : classConstantMatcherFiles) {

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

        return elements.stream().map(stringLiteral -> ((StringLiteralExpression)stringLiteral).getContents()).collect(Collectors.toSet());
    }
}
