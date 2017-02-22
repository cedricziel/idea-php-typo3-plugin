package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtbasePropertyInjectionInspection extends PhpInspection {

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.PERFORMANCE_GROUP_NAME;
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Property injection uses reflection to insert dependencies.\n" +
                "The alternative to property injection is setter-injection.\n" +
                "You should much rather use setter-injection:\n" +
                "<pre>" +
                "protected $myService;\n" +
                "\n" +
                "public function injectMyService(MyService $myService) {\n" +
                "   $this->myService = $myService;\n" +
                "}\n" +
                "</pre>";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpElement(PhpPsiElement element) {
                if (element instanceof PhpDocTag) {
                    PhpDocTag tag = (PhpDocTag) element;
                    if (tag.getName().equals("@inject")) {
                        problemsHolder.registerProblem(element, "Extbase property injection");
                    }
                }
            }
        };
    }
}
