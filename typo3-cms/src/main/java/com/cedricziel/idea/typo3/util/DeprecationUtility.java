package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.*;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.ClassConstImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class DeprecationUtility {
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_CLASS_CONSTANTS = new Key<>("TYPO3_DEPRECATED_CLASS_CONSTANTS");
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_CLASSES = new Key<>("TYPO3_DEPRECATED_CLASSESS");

    public static boolean isDeprecated(@NotNull Project project, @NotNull ClassConstantReference constantReference) {

        ClassConstImpl classConst = (ClassConstImpl) constantReference.resolve();
        if (classConst == null) {
            return false;
        }

        String fqn = classConst.getFQN();
        return getDeprecatedClassConstants(project).contains(fqn);
    }

    public static Set<String> getDeprecatedClassConstants(@NotNull Project project) {
        Set<String> constants = new HashSet<>();
        PsiFile[] classConstantMatcherFiles = FilenameIndex.getFilesByName(project, "ClassConstantMatcher.php", GlobalSearchScope.allScope(project));
        for (PsiFile file : classConstantMatcherFiles) {
            constants.addAll(CachedValuesManager.getManager(project).getCachedValue(
                file,
                DEPRECATED_CLASS_CONSTANTS,
                () -> CachedValueProvider.Result.create(getDeprecatedClassConstantsFromFile(file), PsiModificationTracker.MODIFICATION_COUNT),
                false
            ));
        }

        return constants;
    }

    private static Set<String> getDeprecatedClassConstantsFromFile(@NotNull PsiFile file) {
        PsiElement[] elements = PsiTreeUtil.collectElements(file, el -> PlatformPatterns
            .psiElement(StringLiteralExpression.class)
            .withParent(
                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                    .withAncestor(
                        4,
                        PlatformPatterns.psiElement(PhpElementTypes.RETURN)
                    )
            )
            .accepts(el)
        );

        return Arrays.stream(elements)
            .map(stringLiteral -> ((StringLiteralExpression) stringLiteral).getContents())
            .map(s -> "\\" + s)
            .map(s -> s.replace("::", "."))
            .collect(Collectors.toSet());
    }

    public static Set<String> getDeprecatedClassNames(@NotNull Project project) {
        Set<String> classNames = new HashSet<>();

        PsiFile[] classNameMatcherFiles = FilenameIndex.getFilesByName(project, "ClassNameMatcher.php", GlobalSearchScope.allScope(project));
        for (PsiFile file : classNameMatcherFiles) {
            classNames.addAll(CachedValuesManager.getManager(project).getCachedValue(
                file,
                DEPRECATED_CLASSES,
                () -> CachedValueProvider.Result.create(getDeprecatedClassNamesFromFile(file), PsiModificationTracker.MODIFICATION_COUNT),
                false
            ));
        }

        return classNames;
    }

    private static Set<String> getDeprecatedClassNamesFromFile(@NotNull PsiFile file) {
        PsiElement[] elements = PsiTreeUtil.collectElements(file, el -> PlatformPatterns
            .psiElement(StringLiteralExpression.class)
            .withParent(
                PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                    .withAncestor(
                        4,
                        PlatformPatterns.psiElement(PhpElementTypes.RETURN)
                    )
            )
            .accepts(el));

        return Arrays.stream(elements)
            .map(stringLiteral -> "\\" + ((StringLiteralExpression) stringLiteral).getContents())
            .collect(Collectors.toSet());
    }
}
