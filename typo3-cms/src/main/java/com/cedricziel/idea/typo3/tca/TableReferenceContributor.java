package com.cedricziel.idea.typo3.tca;

import com.cedricziel.idea.typo3.util.PhpLangUtil;
import com.cedricziel.idea.typo3.util.TCAUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TableReferenceContributor extends PsiReferenceContributor {

    static final Map<String, Integer> POSITIONAL_ARGUMENTS;

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // table name as string in PHP Code
        registrar.registerReferenceProvider(
            TCAPatterns.tableNameAsArrayValue(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        // known array hashes in TCA definitions
                        if (TCAUtil.arrayIndexIsTCATableNameField(element)) {
                            return new PsiReference[]{new TableReference((StringLiteralExpression) element)};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                }
        );

        // table name as named argument
        registrar.registerReferenceProvider(
                getTableNameArgumentPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression stringElement = (StringLiteralExpression) element;
                        String argumentName = PhpLangUtil.getParameterName(stringElement);
                        if (argumentName != null && argumentName.equals("table")) {
                            return new PsiReference[]{new TableReference(stringElement)};
                        }

                        if (argumentName != null && argumentName.equals("tableName")) {
                            return new PsiReference[]{new TableReference(stringElement)};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                }
        );

        // known positional arguments
        registrar.registerReferenceProvider(
                getTableNameArgumentPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

                        StringLiteralExpression stringElement = (StringLiteralExpression) element;
                        String methodName = PhpLangUtil.getMethodName(element);
                        String fqn = PhpLangUtil.getClassName(element);

                        String needle = fqn + "::" + methodName;

                        if (POSITIONAL_ARGUMENTS.containsKey(needle)) {
                            int parameterPosition = PhpLangUtil.getParameterPosition(element);
                            int position = POSITIONAL_ARGUMENTS.get(needle) - 1;

                            if (parameterPosition == position) {
                                 return new PsiReference[]{new TableReference(stringElement)};
                             }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                }
        );
    }

    private PsiElementPattern.Capture<StringLiteralExpression> getTableNameArgumentPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class).withSuperParent(
                2,
                PlatformPatterns.psiElement(MethodReference.class)
        );
    }

    static {

        POSITIONAL_ARGUMENTS = Map.ofEntries(Map.entry("\\TYPO3\\CMS\\Core\\Utility\\ExtensionManagementUtility::makeCategorizable", 2),

                // TYPO3 CMS QueryBuilder
                Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::delete", 1), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::update", 1), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::insert", 1), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::join", 2), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::innerJoin", 2), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::leftJoin", 2), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::rightJoin", 2), Map.entry("\\TYPO3\\CMS\\Core\\Database\\Query\\QueryBuilder::quoteIdentifier", 1),

                // Doctrine DBAL QueryBuilder
                Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::delete", 1), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::update", 1), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::insert", 1), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::from", 1), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::join", 2), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::innerJoin", 2), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::leftJoin", 2), Map.entry("\\Doctrine\\DBAL\\Query\\QueryBuilder::rightJoin", 2), Map.entry("\\TYPO3\\CMS\\Core\\Database\\ConnectionPool::getConnectionForTable", 1), Map.entry("\\TYPO3\\CMS\\Core\\Database\\ConnectionPool::getQueryBuilderForTable", 1));
    }
}
