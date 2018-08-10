package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

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
        "cols",
        "rows",
    };

    public static final String[] TCA_DEFAULT_EVALUATIONS = {
        "alpha",
        "alphanum",
        "alphanum_x",
        "date",
        "datetime",
        "domainname",
        "double2",
        "int",
        "is_in",
        "lower",
        "md5",
        "nospace",
        "null",
        "num",
        "password",
        "required",
        "time",
        "timesec",
        "trim",
        "unique",
        "uniqueInPid",
        "upper",
        "year",
    };

    public static final String[] TCA_CONFIG_SECTION_CHILDREN = {
        "appearance",
        "allowNonIdValues",
        "authMode",
        "authMode_enforce",
        "autoSizeMax",
        "allowed",
        "behaviour",
        "checkbox",
        "cols",
        "dbType",
        "default",
        "disableNoMatchingValueElement",
        "disable_controls",
        "disallowed",
        "dontRemapTablesOnCopy",
        "ds",
        "ds_tableField",
        "ds_pointerField",
        "ds_pointerField_searchParent",
        "ds_pointerField_searchParent_subField",
        "enableMultiSelectFilterTextfield",
        "exclusiveKeys",
        "eval",
        "items",
        "itemsProcFunc",
        "itemListStyle",
        "filter",
        "fixedRows",
        "fieldWizard",
        "fileFolder",
        "fileFolder_extList",
        "fileFolder_recursions",
        "form_type",
        "format",
        "foreign_match_fields",
        "foreign_default_sortby",
        "foreign_field",
        "foreign_label",
        "foreign_record_defaults",
        "foreign_sortby",
        "foreign_selector",
        "foreign_selector_fieldTcaOverride",
        "foreign_table",
        "foreign_table_field",
        "foreign_table_loadIcons",
        "foreign_table_prefix",
        "foreign_table_where",
        "foreign_types",
        "foreign_unique",
        "is_in",
        "internal_type",
        "iconsInOptionTags",
        "localizeReferencesAtParentLocalization",
        "max",
        "mode",
        "MM",
        "MM_oppositeUsage",
        "MM_hasUidField",
        "MM_insert_fields",
        "MM_match_fields",
        "MM_opposite_field",
        "MM_table_where",
        "maxitems",
        "minitems",
        "max_size",
        "multiple",
        "multiSelectFilterItems",
        "neg_foreign_table",
        "noTableWrapping",
        "noIconsBelowSelect",
        "parameters",
        "pass_content",
        "placeholder",
        "prepend_tname",
        "range",
        "renderType",
        "renderMode",
        "rootLevel",
        "rows",
        "show_thumbs",
        "selicon_cols",
        "search",
        "selectedListStyle",
        "showIfRte",
        "size",
        "softref",
        "special",
        "suppress_icons",
        "symmetric_field",
        "symmetric_label",
        "symmetric_sortby",
        "readOnly",
        "treeConfig",
        "type",
        "userFunc",
        "uploadfolder",
        "validation",
        "wizards",
        "wrap",
    };

    private static final String EXT_LOCALCONF_FILENAME = "ext_localconf.php";
    private static final String NODE_FACTORY_CLASS = "TYPO3\\CMS\\Backend\\Form\\NodeFactory";

    private static Key<CachedValue<Set<String>>> RENDER_TYPES_KEY = new Key<>("TYPO3_CMS_RENDER_TYPES");

    public static boolean arrayIndexIsTCATableNameField(PsiElement element) {
        String arrayIndex = extractArrayIndexFromValue(element);

        return Arrays.asList(TCA_TABLE_FIELDS).contains(arrayIndex);
    }

    @NotNull
    public static Set<String> getAvailableRenderTypes(PsiElement element) {
        return getAvailableRenderTypes(element.getProject());
    }

    @NotNull
    private synchronized static Set<String> getAvailableRenderTypes(Project project) {
        CachedValue<Set<String>> cachedRenderTypes = project.getUserData(RENDER_TYPES_KEY);
        if (cachedRenderTypes != null && cachedRenderTypes.hasUpToDateValue()) {
            return cachedRenderTypes.getValue();
        }

        CachedValue<Set<String>> cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
            Set<String> renderTypes = new HashSet<>();
            Set<PsiElement> elementsFromExtLocalConf = findAvailableRenderTypes(project);

            // add static list of render types
            renderTypes.addAll(Arrays.asList(TCA_CORE_RENDER_TYPES));

            // add dynamic list of render types from nodeRegistry
            for (PsiElement el : elementsFromExtLocalConf) {
                if (el instanceof StringLiteralExpression) {
                    renderTypes.add(((StringLiteralExpression) el).getContents());
                }
            }

            return CachedValueProvider.Result.create(renderTypes, PsiModificationTracker.MODIFICATION_COUNT);
        }, false);

        project.putUserData(RENDER_TYPES_KEY, cachedValue);

        return cachedValue.getValue();
    }

    public static Set<String> getAvailableColumnTypes() {

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

    public static boolean insideTCAColumnDefinition(PsiElement element) {
        return PlatformPatterns.psiElement()
            .withAncestor(
                11,
                PlatformPatterns
                    .psiElement(PhpElementTypes.HASH_ARRAY_ELEMENT)
                    .withChild(
                        PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).withText("'columns'")
                    )
            )
            .accepts(element);
    }

    @NotNull
    public static Set<String> getAvailableEvaluations() {
        Set<String> evaluations = new THashSet<>();
        evaluations.addAll(Arrays.asList(TCA_DEFAULT_EVALUATIONS));

        return evaluations;
    }

    public static String[] getConfigSectionChildren() {
        return TCA_CONFIG_SECTION_CHILDREN;
    }
}
