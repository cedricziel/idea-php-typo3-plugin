package com.cedricziel.idea.typo3.codeInsight;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PathResourceCompletionContributor extends CompletionContributor {
    public PathResourceCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(StringLiteralExpression.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();

                        Project project = position.getProject();
                        PsiElement parent = position.getParent();
                        if (!(parent instanceof StringLiteralExpression)) {
                            return;
                        }

                        String currentText = parent.getText();
                        if (project.getBasePath() == null || !currentText.contains("EXT:")) {
                            return;
                        }

                        String[] includes;
                        if (currentText.contains("/")) {
                            String current = currentText
                                    .split("/", 1)[0]
                                    .replace("EXT:", "")
                                    .replace("IntellijIdeaRulezzz", "")
                                    .replace("'", "")
                                    .trim();

                            includes = new String[]{
                                    "**/sysext/" + current + "**",
                                    "**/typo3conf/ext/" + current + "**",
                            };
                        } else {
                            includes = new String[]{
                                    "**/sysext/*/",
                                    "**/typo3conf/ext/*/",
                            };
                        }

                        DirectoryScanner scanner = new DirectoryScanner();

                        scanner.setBasedir(project.getBasePath());
                        scanner.addDefaultExcludes();
                        scanner.setIncludes(includes);
                        scanner.scan();

                        Arrays.stream(scanner.getIncludedFiles()).forEach(f -> {
                            result.addElement(new LookupElement() {
                                @NotNull
                                @Override
                                public String getLookupString() {
                                    String[] splitted = f.split("sysext/");
                                    if (splitted.length > 1) {
                                        return "EXT:" + splitted[1];
                                    }

                                    splitted = f.split("typo3conf/ext/");
                                    if (splitted.length > 1) {
                                        return "EXT:" + splitted[1];
                                    }

                                    return "EXT:" + f;
                                }
                            });
                        });

                        Arrays.stream(scanner.getIncludedDirectories()).forEach(f -> {
                            result.addElement(new LookupElement() {

                                @Override
                                public void renderElement(LookupElementPresentation presentation) {
                                    presentation.setIcon(AllIcons.Modules.SourceFolder);

                                    super.renderElement(presentation);
                                }

                                @NotNull
                                @Override
                                public String getLookupString() {
                                    String[] splitted = f.split("sysext/");
                                    if (splitted.length > 1) {
                                        return "EXT:" + splitted[1];
                                    }

                                    splitted = f.split("typo3conf/ext/");
                                    if (splitted.length > 1) {
                                        return "EXT:" + splitted[1];
                                    }

                                    return "EXT:" + f;
                                }
                            });
                        });
                    }
                }
        );
    }
}
