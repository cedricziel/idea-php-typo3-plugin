package com.cedricziel.idea.typo3.util;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignalSlotDispatcherUtil {
    @NotNull
    public static PsiElement[] getSignalPsiElements(final Project project, String fqn, final String methodName) {
        List<PsiElement> foundElements = new ArrayList<>();

        PhpIndex.getInstance(project).getClassesByFQN(fqn).forEach(c -> {
            Method methodByName = c.findMethodByName(methodName);
            if (methodByName != null) {
                foundElements.add(methodByName);
            }
        });

        return foundElements.toArray(PsiElement.EMPTY_ARRAY);
    }

    public static LookupElement[] getPossibleSlotMethodLookupElements(@NotNull Project project, @NotNull String fqn) {
        List<LookupElement> lookupElements = new ArrayList<>();
        PhpIndex.getInstance(project).getClassesByFQN(fqn).forEach(c -> {
            for (Method method : c.getMethods()) {
                if (method.getModifier().isPublic()) {
                    lookupElements.add(LookupElementBuilder.createWithIcon(method));
                }
            }
        });

        return lookupElements.toArray(LookupElement.EMPTY_ARRAY);
    }
}
