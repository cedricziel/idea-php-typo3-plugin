package com.cedricziel.idea.typo3.util;

import com.intellij.psi.PsiElement;

import java.util.Arrays;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;

public class TCAUtil {

    public static final String[] TCA_TABLE_FIELDS = {
            "foreign_table",
            "allowed",
    };

    public static boolean arrayIndexIsTCATableNameField(PsiElement element) {
        String arrayIndex = extractArrayIndexFromValue(element);

        return Arrays.asList(TCA_TABLE_FIELDS).contains(arrayIndex);
    }
}
