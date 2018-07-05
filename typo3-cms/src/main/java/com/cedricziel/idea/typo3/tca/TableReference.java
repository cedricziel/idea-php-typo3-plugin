package com.cedricziel.idea.typo3.tca;

import com.cedricziel.idea.typo3.util.TableUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import static com.cedricziel.idea.typo3.util.TableUtil.getTableDefinitionElements;

public class TableReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String tablename;

    public TableReference(@NotNull StringLiteralExpression element) {
        super(element);

        this.tablename = element.getContents();
    }

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     *                       being resolved is considered incomplete, and the method may return additional
     *                       invalid results.
     * @return the array of results for resolving the reference.
     */
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(getTableDefinitionElements(tablename, myElement.getProject()));
    }

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        return TableUtil.createAvailableTableNamesLookupElements(myElement.getProject());
    }
}
