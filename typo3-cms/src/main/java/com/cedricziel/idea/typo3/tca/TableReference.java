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

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return PsiElementResolveResult.createResults(getTableDefinitionElements(tablename, myElement.getProject()));
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return TableUtil.createAvailableTableNamesLookupElements(myElement.getProject());
    }
}
