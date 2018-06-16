package com.cedricziel.idea.typo3.tca.codeInspection.migrations;

import com.cedricziel.idea.typo3.tca.TCAPatterns;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.inspections.quickfix.PhpQuickFixBase;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MigrateT3editorWizardToRenderTypeInspection extends TCAMigrationInspection {

    private final MigrateT3editorWizardToRenderTypeQuickFix myQuickFix = new MigrateT3editorWizardToRenderTypeQuickFix();

    @Nullable
    static ArrayHashElement findTcaConfigArrayCreationExpression(@NotNull ArrayCreationExpression expression) {

        return (ArrayHashElement) PsiTreeUtil.findFirstParent(expression, psiElement -> {

            if (!(psiElement instanceof ArrayHashElement)) {
                return false;
            }

            return ((ArrayHashElement) psiElement).getKey() instanceof StringLiteralExpression && ((ArrayHashElement) psiElement).getKey() != null && ((StringLiteralExpression) ((ArrayHashElement) psiElement).getKey()).getContents().equals("config");
        });
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpArrayCreationExpression(ArrayCreationExpression expression) {
                super.visitPhpArrayCreationExpression(expression);

                if (!TCAPatterns.isWizard(expression)) {
                    return;
                }

                if (!TCAPatterns.hasArrayHashElement(expression, "userFunc", "TYPO3\\\\CMS\\\\T3editor\\\\FormWizard->main")) {
                    return;
                }

                holder.registerProblem(expression, "T3editor wizard usage is deprecated with v7.6", myQuickFix);
            }
        };
    }

    private static class MigrateT3editorWizardToRenderTypeQuickFix extends PhpQuickFixBase {
        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getName() {
            return "Migrate T3Editor usage to renderType=t3editor";
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            ArrayCreationExpression psiElement = (ArrayCreationExpression) descriptor.getPsiElement();

            ArrayHashElement configSection = MigrateT3editorWizardToRenderTypeInspection.findTcaConfigArrayCreationExpression(psiElement);
            if (configSection == null || !(configSection.getValue() instanceof ArrayCreationExpression)) {
                return;
            }

            if (TCAPatterns.hasArrayHashElement((ArrayCreationExpression) configSection.getValue(), "renderType")) {
                ((ArrayCreationExpression) configSection.getValue()).getHashElements().forEach(x -> {
                    PhpPsiElement key = x.getKey();
                    if (key instanceof StringLiteralExpression && ((StringLiteralExpression) key).getContents().equals("renderType")) {
                        while (!(x.getNextSibling() instanceof ArrayHashElement) ) {
                            x.getNextSibling().delete();
                        }

                        x.delete();
                    }
                });
            }

            ArrayHashElement renderTypeSection = PhpPsiElementFactory.createPhpPsiFromText(project, ArrayHashElement.class, "['renderType' => 't3editor'],");

            ArrayHashElement valueArray = PsiTreeUtil.findChildOfType(configSection.getValue(), ArrayHashElement.class);
            if (valueArray != null) {
                configSection.getValue().add(renderTypeSection);
                PsiElement place = configSection.getValue().add(PhpPsiElementFactory.createComma(project));

                final Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, psiElement.getContainingFile().getVirtualFile()), true);
                if (editor == null) {
                    return;
                }

                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                editor.getDocument().insertString(place.getTextOffset() + 2, "\n");

                CodeStyleManager.getInstance(project).reformatText(psiElement.getContainingFile(), configSection.getTextOffset(), valueArray.getTextOffset());
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

                PsiElement firstParent = PsiTreeUtil.findFirstParent(psiElement, x -> x instanceof ArrayHashElement);
                if (firstParent != null) {
                    firstParent.delete();
                }

                ArrayCreationExpression arrayCreation = (ArrayCreationExpression)PsiTreeUtil.findFirstParent(psiElement, x -> x instanceof ArrayCreationExpression);
                if (arrayCreation == null) {
                    return;
                }

                if (!arrayCreation.getHashElements().iterator().hasNext()) {
                    arrayCreation.delete();
                }
            }
        }
    }
}
