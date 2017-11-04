package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import java.util.*;

import static com.cedricziel.idea.typo3.psi.PhpElementsUtil.extractArrayIndexFromValue;

public class TCAUtil {

    public static final String[] TCA_TABLE_FIELDS = {
            "foreign_table",
            "allowed",
    };

    public static final String[] TCA_CORE_TYPES = {
            "check",
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

    public static final String[] TCA_NUMERIC_CONFIG_KEYS = {
            "size",
            "min",
            "max",
            "maxitems",
            "autoSizeMax",
    };

    private static final String EXT_LOCALCONF_FILENAME = "ext_localconf.php";

    private static final String NODE_FACTORY_CLASS = "TYPO3\\CMS\\Backend\\Form\\NodeFactory";

    public static boolean arrayIndexIsTCATableNameField(PsiElement element) {
        String arrayIndex = extractArrayIndexFromValue(element);

        return Arrays.asList(TCA_TABLE_FIELDS).contains(arrayIndex);
    }

    public static Set<String> getAvailableRenderTypes(PsiElement element) {
        Set<String> renderTypes = new HashSet<>();
        Set<PsiElement> elementsFromExtLocalConf = findAvailableRenderTypes(element.getProject());

        // add static list of render types
        renderTypes.addAll(Arrays.asList(TCA_CORE_RENDER_TYPES));

        // add dynamic list of render types from nodeRegistry
        for (PsiElement el : elementsFromExtLocalConf) {
            if (el instanceof StringLiteralExpression) {
                renderTypes.add(((StringLiteralExpression) el).getContents());
            }
        }

        return renderTypes;
    }

    public static Set<String> getAvailableColumnTypes(PsiElement element) {

        return new HashSet<>(Arrays.asList(TCA_CORE_TYPES));
    }

    private static Set<PsiElement> findAvailableRenderTypes(Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        PsiFile[] extLocalConfFiles = FilenameIndex.getFilesByName(project, EXT_LOCALCONF_FILENAME, GlobalSearchScope.allScope(project));
        Collection<PhpClass> nodeRegistries = phpIndex.getClassesByFQN(NODE_FACTORY_CLASS);

        Set<PsiElement> elements = new HashSet<>();

        for (PhpClass registry : nodeRegistries) {
            Collections.addAll(
                    elements,
                    PsiTreeUtil.collectElements(registry, element -> PlatformPatterns
                            .psiElement(StringLiteralExpression.class)
                            .withParent(
                                    PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY)
                                            .withAncestor(
                                                    3,
                                                    PlatformPatterns.psiElement(PhpElementTypes.CLASS_FIELD).withName("nodeTypes")
                                            )
                            )
                            .accepts(element)
                    )
            );
        }

        for (PsiFile file : extLocalConfFiles) {
            Collections.addAll(
                    elements,
                    PsiTreeUtil.collectElements(file, element -> PlatformPatterns
                            .psiElement(StringLiteralExpression.class)
                            .withParent(
                                    PlatformPatterns
                                            .psiElement(PhpElementTypes.ARRAY_VALUE)
                                            .withParent(
                                                    PlatformPatterns
                                                            .psiElement(PhpElementTypes.HASH_ARRAY_ELEMENT)
                                                            .withChild(
                                                                    PlatformPatterns
                                                                            .psiElement(PhpElementTypes.ARRAY_KEY)
                                                                            .withText("'nodeName'")
                                                            )
                                            )
                            ).accepts(element))
            );
        }

        return elements;
    }
}
