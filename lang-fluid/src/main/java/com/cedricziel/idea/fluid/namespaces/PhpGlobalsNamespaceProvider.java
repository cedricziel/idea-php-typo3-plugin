package com.cedricziel.idea.fluid.namespaces;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PhpGlobalsNamespaceProvider implements NamespaceProvider {
    @NotNull
    @Override
    public Collection<FluidNamespace> provideForElement(@NotNull PsiElement element) {
        List<FluidNamespace> namespaces = new ArrayList<>();

        Project project = element.getProject();
        for (PsiFile psiFile : FilenameIndex.getFilesByName(project, "ext_localconf.php", GlobalSearchScope.allScope(project))) {
            GlobalsNamespaceVisitor visitor = new GlobalsNamespaceVisitor();
            psiFile.accept(visitor);

            namespaces.addAll(visitor.namespaces);
        }

        return namespaces;
    }

    private class GlobalsNamespaceVisitor extends PhpRecursiveElementVisitor {
        public List<FluidNamespace> namespaces = new ArrayList<>();

        @Override
        public void visitPhpArrayIndex(ArrayIndex arrayIndex) {
            if (getArrayKeyPattern().accepts(arrayIndex)) {
                AssignmentExpression assignmentExpression = (AssignmentExpression) PsiTreeUtil.findFirstParent(arrayIndex, el -> el instanceof AssignmentExpression);
                if (assignmentExpression != null && assignmentExpression.getValue() instanceof ArrayCreationExpression) {
                    if (arrayIndex.getFirstPsiChild() instanceof StringLiteralExpression) {
                        String prefix = ((StringLiteralExpression) arrayIndex.getFirstPsiChild()).getContents();
                        PhpPsiElement assignedValue = assignmentExpression.getValue();

                        for (PsiElement element : assignedValue.getChildren()) {
                            if (!PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(element)) {
                                continue;
                            }

                            for (PsiElement child : element.getChildren()) {
                                if (PlatformPatterns.psiElement(StringLiteralExpression.class).accepts(child)) {
                                    String foundNamespace = ((StringLiteralExpression) child).getContents();
                                    namespaces.add(new FluidNamespace(prefix, foundNamespace.replace("\\\\", "/")));
                                }
                            }
                        }
                    }
                }
            }

            if (getEmptyArrayKeyPattern().accepts(arrayIndex)) {
                AssignmentExpression assignmentExpression = (AssignmentExpression) PsiTreeUtil.findFirstParent(arrayIndex, el -> el instanceof AssignmentExpression);
                if (assignmentExpression != null && assignmentExpression.getValue() instanceof StringLiteralExpression) {
                    String namespace = ((StringLiteralExpression) assignmentExpression.getValue()).getContents();
                    if (arrayIndex.getFirstPsiChild() instanceof StringLiteralExpression) {
                        String prefix = ((StringLiteralExpression) arrayIndex.getFirstPsiChild()).getContents();

                        namespaces.add(new FluidNamespace(prefix, namespace.replace("\\\\", "/")));
                    }
                }
            }

            super.visitPhpArrayIndex(arrayIndex);
        }

        @NotNull
        private PsiElementPattern.Capture<ArrayIndex> getEmptyArrayKeyPattern() {
            return PlatformPatterns.psiElement(ArrayIndex.class).withParent(
                PlatformPatterns.psiElement(ArrayAccessExpression.class).withParent(
                    PlatformPatterns.psiElement(ArrayAccessExpression.class).withParent(
                        PlatformPatterns.psiElement(AssignmentExpression.class)
                    )
                ).withChild(
                    PlatformPatterns.psiElement(ArrayAccessExpression.class)
                        .withChild(
                            PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'namespaces'"))
                        )
                        .withChild(
                            PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'fluid'"))).withChild(
                                PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'SYS'"))).withChild(
                                    PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'TYPO3_CONF_VARS'")))
                                )
                            )
                        )
                )
            );
        }

        @NotNull
        private PsiElementPattern.Capture<ArrayIndex> getArrayKeyPattern() {
            return PlatformPatterns.psiElement(ArrayIndex.class).withParent(
                PlatformPatterns.psiElement(ArrayAccessExpression.class).withParent(
                    PlatformPatterns.psiElement(AssignmentExpression.class)
                ).withChild(
                    PlatformPatterns.psiElement(ArrayAccessExpression.class)
                        .withChild(
                            PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'namespaces'"))
                        )
                        .withChild(
                            PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'fluid'"))).withChild(
                                PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'SYS'"))).withChild(
                                    PlatformPatterns.psiElement(ArrayAccessExpression.class).withChild(PlatformPatterns.psiElement(ArrayIndex.class).withText(PlatformPatterns.string().oneOf("'TYPO3_CONF_VARS'")))
                                )
                            )
                        )
                )
            );
        }
    }
}
