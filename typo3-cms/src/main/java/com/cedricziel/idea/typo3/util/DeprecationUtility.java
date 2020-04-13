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
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.ClassConstImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpDefineImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeprecationUtility {
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_CLASS_CONSTANTS = new Key<>("TYPO3_DEPRECATED_CLASS_CONSTANTS");
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_CLASSES = new Key<>("TYPO3_DEPRECATED_CLASSES");
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_CONSTANTS = new Key<>("TYPO3_DEPRECATED_CONSTANTS");
    private static final Key<CachedValue<Collection<String>>> DEPRECATED_FUNCTION_CALLS = new Key<>("TYPO3_DEPRECATED_FUNCTION_CALLS");

    public static boolean isDeprecated(@NotNull Project project, @NotNull ClassConstantReference constantReference) {

        ClassConstImpl classConst = (ClassConstImpl) constantReference.resolve();
        if (classConst == null) {
            return false;
        }

        String fqn = classConst.getFQN();
        return getDeprecatedClassConstants(project).contains(fqn);
    }

    public static boolean isDeprecated(@NotNull Project project, @NotNull ConstantReference constantReference) {

        PhpDefineImpl definition = (PhpDefineImpl) constantReference.resolve();
        if (definition == null) {
            return false;
        }

        String fqn = definition.getFQN();
        return getDeprecatedConstantNames(project).contains(fqn);
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

    public static Set<String> getDeprecatedConstantNames(@NotNull Project project) {
        Set<String> deprecatedConstants = new HashSet<>();

        PsiFile[] constantMatcherFiles = FilenameIndex.getFilesByName(project, "ConstantMatcher.php", GlobalSearchScope.allScope(project));
        for (PsiFile file : constantMatcherFiles) {

            deprecatedConstants.addAll(CachedValuesManager.getManager(project).getCachedValue(
                file,
                DEPRECATED_CONSTANTS,
                () -> CachedValueProvider.Result.create(getDeprecatedConstantNamesFromFile(file), PsiModificationTracker.MODIFICATION_COUNT),
                false
            ));

        }

        return deprecatedConstants;
    }

    private static Set<String> getDeprecatedConstantNamesFromFile(PsiFile file) {
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
            .map(stringLiteral -> "\\" + ((StringLiteralExpression) stringLiteral).getContents())
            .collect(Collectors.toSet());
    }

    public static boolean isDeprecated(@NotNull Project project, @NotNull FunctionReference functionReference) {
        Function definition = (Function) functionReference.resolve();
        if (definition == null) {
            return false;
        }

        String fqn = definition.getFQN();
        return getDeprecatedGlobalFunctionCalls(project).contains(fqn);
    }

    public static Set<String> getDeprecatedGlobalFunctionCalls(@NotNull Project project) {
        Set<String> functionCallNames = new HashSet<>();
        PsiFile[] constantMatcherFiles = FilenameIndex.getFilesByName(project, "FunctionCallMatcher.php", GlobalSearchScope.allScope(project));
        for (PsiFile file : constantMatcherFiles) {

            functionCallNames.addAll(CachedValuesManager.getManager(project).getCachedValue(
                file,
                DEPRECATED_FUNCTION_CALLS,
                () -> CachedValueProvider.Result.create(getDeprecatedGlobalFunctionCallsFromFile(file), PsiModificationTracker.MODIFICATION_COUNT),
                false
            ));
        }

        return functionCallNames;
    }

    private static Set<String> getDeprecatedGlobalFunctionCallsFromFile(PsiFile file) {
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
            .map(stringLiteral -> "\\" + ((StringLiteralExpression) stringLiteral).getContents())
            .collect(Collectors.toSet());
    }
}
