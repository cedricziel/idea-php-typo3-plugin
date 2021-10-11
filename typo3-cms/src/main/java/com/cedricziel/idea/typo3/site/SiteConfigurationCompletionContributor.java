package com.cedricziel.idea.typo3.site;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.*;

import java.util.List;
import java.util.Map;

public class SiteConfigurationCompletionContributor extends CompletionContributor {

    private static final Map<String, String> TOP_LEVEL_KEYS = Map.of("rootPageId", "", "base", "", "languages", "", "errorHandling", "", "routes", "");

    /**
     * languageId: '0'
     * title: English
     * navigationTitle: ''
     * base: /
     * locale: en_US.UTF-8
     * iso-639-1: en
     * hreflang: en-US
     * direction: ltr
     * typo3Language: default
     * flag: gb
     */
    private static final Map<String, String> LANGUAGE_KEYS = Map.of("languageId", "", "title", "", "navigationTitle", "", "base", "", "iso-639-1", "", "hreflang", "", "direction", "", "typo3Language", "", "flag", "");

    /**
     * errorCode: '403'
     * errorHandler: Fluid
     * errorFluidTemplate: 'EXT:my_extension/Resources/Private/Templates/ErrorPages/403.html'
     * errorFluidTemplatesRootPath: 'EXT:my_extension/Resources/Private/Templates/ErrorPages'
     * errorFluidLayoutsRootPath: 'EXT:my_extension/Resources/Private/Layouts/ErrorPages'
     * errorFluidPartialsRootPath
     */
    private static final Map<String, String> ERROR_HANDLING_KEYS = Map.of("errorCode", "", "errorHandler", "",
        // errorHandler: Fluid
        "errorFluidTemplate", "", "errorFluidTemplatesRootPath", "", "errorFluidLayoutsRootPath", "", "errorFluidPartialsRootPath", "",
        // errorHandler: Page
        "errorContentSource", "",
        // errorHandler: PHP
        "errorPhpClassFQCN", "");

    /**
     * route: robots.txt
     * type: staticText
     * content: |
     * Sitemap: https://example.com/sitemap.xml
     * User-agent: *
     * Allow: /
     * Disallow: /forbidden/
     */
    private static final Map<String, String> ROUTES_KEYS = Map.of("route", "", "type", "", "content", "");

    public SiteConfigurationCompletionContributor() {
        // complete top level yaml keys
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY).withSuperParent(2, YAMLMapping.class).withLanguage(YAMLLanguage.INSTANCE), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!TYPO3CMSProjectSettings.isEnabled(parameters.getPosition().getProject())) {
                    return;
                }

                if (!parameters.getOriginalFile().getName().endsWith("config.yaml")) {
                    return;
                }

                addKeysToCompletion(result, TOP_LEVEL_KEYS);
            }
        });

        // complete new top level yaml keys
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(YAMLScalar.class).withLanguage(YAMLLanguage.INSTANCE), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!TYPO3CMSProjectSettings.isEnabled(parameters.getPosition().getProject())) {
                    return;
                }

                if (!parameters.getOriginalFile().getName().endsWith("config.yaml")) {
                    return;
                }

                addKeysToCompletion(result, TOP_LEVEL_KEYS);
            }
        });

        // complete 2nd level yaml keys
        extend(CompletionType.BASIC, yamlKeyChildOfInArray(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!TYPO3CMSProjectSettings.isEnabled(parameters.getPosition().getProject())) {
                    return;
                }

                if (!parameters.getOriginalFile().getName().endsWith("config.yaml")) {
                    return;
                }

                final List<YAMLKeyValue> yamlKeyValues = PsiTreeUtil.collectParents(parameters.getPosition(), YAMLKeyValue.class, false, x -> x instanceof YAMLDocument);
                if (yamlKeyValues.size() <= 0) {
                    return;
                }

                final YAMLKeyValue yamlKeyValue = yamlKeyValues.get(yamlKeyValues.size() - 1);
                final String topLevelKey = yamlKeyValue.getKeyText();

                switch (topLevelKey) {
                    case "languages":
                        addKeysToCompletion(result, LANGUAGE_KEYS);
                        break;
                    case "errorHandling":
                        addKeysToCompletion(result, ERROR_HANDLING_KEYS);
                        break;
                }
            }
        });

        // complete 2nd level known keys
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!TYPO3CMSProjectSettings.isEnabled(parameters.getPosition().getProject())) {
                    return;
                }

                if (!parameters.getOriginalFile().getName().endsWith("config.yaml")) {
                    return;
                }

                final List<YAMLKeyValue> yamlKeyValues = PsiTreeUtil.collectParents(parameters.getPosition(), YAMLKeyValue.class, false, x -> x instanceof YAMLDocument);
                if (yamlKeyValues.size() <= 0) {
                    return;
                }

                final YAMLKeyValue yamlKeyValue = yamlKeyValues.get(yamlKeyValues.size() - 1);
                final String topLevelKey = yamlKeyValue.getKeyText();

                switch (topLevelKey) {
                    case "routes":
                        addKeysToCompletion(result, ROUTES_KEYS);
                        break;
                }
            }
        });
    }

    @NotNull
    private PsiElementPattern.Capture<PsiElement> yamlKeyChildOfInArray() {

        return PlatformPatterns
            .psiElement(YAMLTokenTypes.SCALAR_KEY)
            .withSuperParent(2, YAMLMapping.class)
            .withSuperParent(1, YAMLKeyValue.class)
            .withLanguage(YAMLLanguage.INSTANCE);
    }

    private void addKeysToCompletion(@NotNull CompletionResultSet result, Map<String, String> languageKeys) {
        languageKeys.forEach((key, type) -> {
            final LookupElementBuilder lookupElementBuilder = LookupElementBuilder
                .create(key)
                .withLookupString(key)
                .withTypeText(type)
                .withIcon(TYPO3CMSIcons.SITE_CONFIG_INDEX);

            result.addElement(lookupElementBuilder);
        });
    }
}
