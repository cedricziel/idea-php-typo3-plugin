package com.cedricziel.idea.typo3.codeInspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

public class FluidNamespaceWithoutDataAttributeInspection extends LocalInspectionTool {

    public static final String DATA_NAMESPACE_TYPO_3_FLUID = "data-namespace-typo3-fluid";

    /**
     * Override to provide your own inspection visitor.
     * Created visitor must not be recursive (e.g. it must not inherit {@link PsiRecursiveElementVisitor})
     * since it will be fed with every element in the file anyway.
     * Visitor created must be thread-safe since it might be called on several elements concurrently.
     *
     * @param holder     where visitor will register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return not-null visitor for this inspection.
     * @see PsiRecursiveVisitor
     */
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof XmlTag) {
                    this.visitXmlTag((XmlTag) element);
                }

                super.visitElement(element);
            }

            @Override
            public void visitXmlTag(XmlTag tag) {
                final boolean hasRequiredDataAttribute = tag.getAttribute(DATA_NAMESPACE_TYPO_3_FLUID) != null;
                boolean needsRequiredDataAttribute = false;
                for (XmlAttribute attribute : tag.getAttributes()) {
                    if (!attribute.isNamespaceDeclaration()) {
                        continue;
                    }

                    if (attribute.getValue() == null || !attribute.getValue().contains("typo3.org/ns")) {
                        continue;
                    }

                    if (!tag.getName().equals("html") && !hasRequiredDataAttribute) {
                        needsRequiredDataAttribute = true;

                        break;
                    }
                }

                if (needsRequiredDataAttribute) {
                    holder.registerProblem(tag, "Fluid data-attribute missing", new AddFluidNamespaceDataAttributeFix(tag));
                }
            }
        };
    }

    public static class AddFluidNamespaceDataAttributeFix extends LocalQuickFixOnPsiElement {
        protected AddFluidNamespaceDataAttributeFix(@NotNull XmlTag element) {
            super(element);
        }

        @Override
        public @NotNull @IntentionName String getText() {
            return "Add fluid data attribute";
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
            XmlTag tag = (XmlTag) startElement;

            tag.setAttribute(DATA_NAMESPACE_TYPO_3_FLUID, "true");
        }

        /**
         * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
         * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
         * If the name of the quickfix does not depend on a specific element, simply return {@link #getName()}.
         */
        @Override
        public @NotNull @IntentionFamilyName String getFamilyName() {
            return "Add fluid data attribute";
        }
    }

}
