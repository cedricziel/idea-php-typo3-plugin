package com.cedricziel.idea.typo3.translation;

import com.cedricziel.idea.typo3.index.TranslationIndex;
import com.cedricziel.idea.typo3.util.TranslationUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Translator {
    public static @Nullable
    String translateLLLString(StringLiteralExpression inputString) {
        return translateLLLString(inputString.getProject(), inputString.getContents());
    }

    public static @Nullable
    String translateLLLString(Project project, String inputString) {

        final List<StubTranslation> variants = TranslationIndex.findById(project, inputString);
        StubTranslation defaultTranslation = TranslationUtil.findDefaultTranslationFromVariants(project, variants);

        if (defaultTranslation != null) {
            return TranslationUtil.findPlaceholderTextFor(defaultTranslation);
        }

        return null;
    }
}
