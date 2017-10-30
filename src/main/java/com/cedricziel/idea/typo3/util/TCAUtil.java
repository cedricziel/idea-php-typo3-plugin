package com.cedricziel.idea.typo3.util;

import com.intellij.psi.PsiElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;

public class TCAUtil {

    public static final String[] TCA_TABLE_FIELDS = {
            "foreign_table",
            "allowed",
    };

    public static final String[] TCA_CORE_TYPES = {
            "flex",
            "group",
            "imageManipulation",
            "input",
            "inline",
            "none",
            "passthrough",
            "radio",
            "select",
            "text",
            "user",
    };

    public static final String[] TCA_CORE_RENDER_TYPES = {
            "selectSingle",
            "selectSingleBox",
            "selectCheckBox",
            "selectMultipleSideBySide",
            "selectTree",
            "colorpicker",
            "inputDateTime",
            "inputLink",
            "rsaInput",
            "belayoutwizard",
            "t3editor",
            "textTable",
    };

    public static boolean arrayIndexIsTCATableNameField(PsiElement element) {
        String arrayIndex = extractArrayIndexFromValue(element);

        return Arrays.asList(TCA_TABLE_FIELDS).contains(arrayIndex);
    }

    public static Set<String> getAvailableRenderTypes(PsiElement element) {

        return new HashSet<>(Arrays.asList(TCA_CORE_RENDER_TYPES));
    }

    public static Set<String> getAvailableColumnTypes(PsiElement element) {

        return new HashSet<>(Arrays.asList(TCA_CORE_TYPES));
    }
}
