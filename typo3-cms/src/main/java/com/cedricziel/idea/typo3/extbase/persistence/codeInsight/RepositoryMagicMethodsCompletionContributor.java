package com.cedricziel.idea.typo3.extbase.persistence.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.extbase.ExtbaseUtils;
import com.cedricziel.idea.typo3.util.ExtbaseUtility;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RepositoryMagicMethodsCompletionContributor extends CompletionContributor {
    public RepositoryMagicMethodsCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement(PhpTokenTypes.IDENTIFIER), new ExtbaseRepositoryMagicMethodsCompletionProvider());
    }

    public static class ExtbaseRepositoryMagicMethodsCompletionProvider extends CompletionProvider<CompletionParameters> {

        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            if (!TYPO3CMSProjectSettings.getInstance(parameters.getPosition().getProject()).pluginEnabled) {

                return;
            }

            PsiElement position = parameters.getPosition().getOriginalElement();

            if (!(position.getParent() instanceof FieldReference) && !(position.getParent() instanceof MethodReference)) {
                return;
            }

            if (!(position.getParent().getFirstChild() instanceof Variable) && !(position.getParent().getFirstChild() instanceof FieldReference)) {
                return;
            }

            PhpTypedElement variable = (PhpTypedElement) position.getParent().getFirstChild();

            PhpType type = variable.getType();
            Project project = position.getProject();
            type.getTypes().forEach(t -> {
                Collection<PhpClass> classesByFQN = PhpIndex.getInstance(project).getClassesByFQN(t);
                classesByFQN
                        .stream()
                        .filter(ExtbaseUtils::isRepositoryClass)
                        .forEach(c -> createLookupElementsForRepository(project, c, result));
            });
        }

        private void createLookupElementsForRepository(@NotNull Project project,  @NotNull PhpClass repositoryClass, @NotNull CompletionResultSet result) {
            String potentialModelClass = ExtbaseUtility.convertRepositoryFQNToEntityFQN(repositoryClass.getFQN());

            Collection<PhpClass> classesByFQN = PhpIndex.getInstance(project).getClassesByFQN(potentialModelClass);
            classesByFQN.forEach(c -> c.getFields().forEach(f -> {
                if (!ExtbaseUtils.fieldHasMagicFinders(f)) {
                    return;
                }

                // findBy* matches on single fields only and not on collections
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

                        presentation.setTailText("(" + f.getName() + " : " + f.getInferredType() + ")", true);
                        presentation.setTypeText(c.getName() + "[]|" + ExtbaseUtils.QUERY_RESULT_INTERFACE);
                    }
                });

                // countBy* matches on single fields only and not on collections
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
                        presentation.setTailText("(" + f.getName() + " : " + f.getInferredType() + ")", true);
                        presentation.setTypeText("int");
                    }
                });

                // findOneBy* matches on single fields only and not on collections
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
                        presentation.setTailText("(" + f.getName() + " : " + f.getInferredType() + ")", true);
                        presentation.setTypeText("null|" + c.getName());
                    }
                });
            }));
        }
    }
}
