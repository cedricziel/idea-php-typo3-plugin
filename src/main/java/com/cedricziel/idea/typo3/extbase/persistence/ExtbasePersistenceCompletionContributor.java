package com.cedricziel.idea.typo3.extbase.persistence;

import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class ExtbasePersistenceCompletionContributor extends CompletionContributor {
    public ExtbasePersistenceCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement(PhpTokenTypes.IDENTIFIER), new ExtbaseRepositoryMagicMethodsCompletionProvider());
    }

    public static class ExtbaseRepositoryMagicMethodsCompletionProvider extends CompletionProvider<CompletionParameters> {

        public static final String TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY = "TYPO3\\CMS\\Extbase\\Persistence\\Repository";
        private static final String QUERY_RESULT_INTERFACE = "TYPO3\\CMS\\Extbase\\Persistence\\QueryResultInterface";
        private static final String OBJECT_STORAGE = "TYPO3\\CMS\\Extbase\\Persistence\\ObjectStorage";

        private ExtbaseRepositoryMagicMethodsCompletionProvider() {
        }

        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition().getOriginalElement();

            if (!(position.getParent() instanceof FieldReference) && !(position.getParent() instanceof MethodReference)) {
                return;
            }

            Iterator<PhpClass> iterator = PhpIndex.getInstance(position.getProject()).getClassesByFQN(TYPO3_CMS_EXTBASE_PERSISTENCE_REPOSITORY).iterator();
            if (!iterator.hasNext()) {
                return;
            }

            PhpClass repositoryClass = iterator.next();
            if (repositoryClass == null) {
                return;
            }

            if (!(position.getParent().getFirstChild() instanceof Variable) && !(position.getParent().getFirstChild() instanceof FieldReference)) {
                return;
            }

            PhpTypedElement variable;
            if (position.getParent().getFirstChild() instanceof Variable) {
                variable = (PhpTypedElement) position.getParent().getFirstChild();
            } else {
                variable = (PhpTypedElement) position.getParent().getFirstChild();
            }

            PhpType type = variable.getType();
            type.getTypes().forEach(t -> {
                Collection<PhpClass> classesByFQN = PhpIndex.getInstance(position.getProject()).getClassesByFQN(t);
                classesByFQN.forEach(c -> {
                    if (PhpClassHierarchyUtils.isSuperClass(repositoryClass, c, true)) {
                        createLookupElementsForRepository(variable, c, result);
                    }
                });
            });
        }

        private void createLookupElementsForRepository(@NotNull PhpTypedElement position, @NotNull PhpClass repositoryClass, @NotNull CompletionResultSet result) {
            String repositoryClassFqn = repositoryClass.getFQN();

            String potentialModelClass = ExtbaseUtility.convertRepositoryFQNToEntityFQN(repositoryClassFqn);

            Collection<PhpClass> classesByFQN = PhpIndex.getInstance(position.getProject()).getClassesByFQN(potentialModelClass);
            classesByFQN.forEach(c -> {
                c.getFields().forEach(f -> {
                    if (f.isConstant() || f.isInternal() || f.getName().equals("_cleanProperties") || f.getName().equals("_isClone")) {
                        return;
                    }

                    // findBy* matches on single fields only and not on collections
                    if (!f.getDeclaredType().toString().contains(OBJECT_STORAGE)) {
                        result.addElement(new LookupElement() {
                            @NotNull
                            @Override
                            public String getLookupString() {
                                return "findBy" + StringUtils.capitalize(f.getName()) + "($" + f.getName() + ")";
                            }

                            @Override
                            public void renderElement(LookupElementPresentation presentation) {
                                super.renderElement(presentation);

                                presentation.setItemText("findBy" + StringUtils.capitalize(f.getName()));
                                presentation.setTypeText("findBy" + StringUtils.capitalize(f.getName()));
                                presentation.setIcon(PhpIcons.METHOD_ICON);
                                presentation.setTailText("(" + f.getName() + " : " + f.getDeclaredType() + ")", true);
                                presentation.setTypeText(c.getName() + "[]|" + QUERY_RESULT_INTERFACE);
                            }
                        });
                    }

                    // countBy* matches on single fields only and not on collections
                    if (!f.getDeclaredType().toString().contains(OBJECT_STORAGE)) {
                        result.addElement(new LookupElement() {
                            @NotNull
                            @Override
                            public String getLookupString() {
                                return "countBy" + StringUtils.capitalize(f.getName()) + "($" + f.getName() + ")";
                            }

                            @Override
                            public void renderElement(LookupElementPresentation presentation) {
                                super.renderElement(presentation);

                                presentation.setItemText("countBy" + StringUtils.capitalize(f.getName()));
                                presentation.setTypeText("countBy" + StringUtils.capitalize(f.getName()));
                                presentation.setIcon(PhpIcons.METHOD_ICON);
                                presentation.setTailText("(" + f.getName() + " : " + f.getDeclaredType() + ")", true);
                                presentation.setTypeText("int");
                            }
                        });
                    }

                    // findOneBy* matches on single fields only and not on collections
                    if (!f.getDeclaredType().toString().contains(OBJECT_STORAGE)) {
                        result.addElement(new LookupElement() {
                            @NotNull
                            @Override
                            public String getLookupString() {
                                return "findOneBy" + StringUtils.capitalize(f.getName()) + "($" + f.getName() + ")";
                            }

                            @Override
                            public void renderElement(LookupElementPresentation presentation) {
                                super.renderElement(presentation);

                                presentation.setItemText("findOneBy" + StringUtils.capitalize(f.getName()));
                                presentation.setTypeText("findOneBy" + StringUtils.capitalize(f.getName()));
                                presentation.setIcon(PhpIcons.METHOD_ICON);
                                presentation.setTailText("(" + f.getName() + " : " + f.getDeclaredType() + ")", true);
                                presentation.setTypeText("null|" + c.getName());
                            }
                        });
                    }
                });
            });
        }
    }
}