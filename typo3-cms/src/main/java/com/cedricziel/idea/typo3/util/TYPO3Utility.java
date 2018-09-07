package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.text.VersionComparatorUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Constant;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TYPO3Utility {

    private static final Map<String, String> DEFAULT_ASPECTS = new HashMap<String, String>() {{
        put("date", "\\TYPO3\\CMS\\Core\\Context\\DateTimeAspect");
        put("visibility", "\\TYPO3\\CMS\\Core\\Context\\VisibilityAspect");
        put("backend.user", "\\TYPO3\\CMS\\Core\\Context\\UserAspect");
        put("frontend.user", "\\TYPO3\\CMS\\Core\\Context\\UserAspect");
        put("workspace", "\\TYPO3\\CMS\\Core\\Context\\WorkspaceAspect");
        put("language", "\\TYPO3\\CMS\\Core\\Context\\LanguageAspect");
    }};

    @Nullable
    public static Constant getVersionConstant(@NotNull Project project) {
        Iterator<Constant> typo3_version = PhpIndex
                .getInstance(project)
                .getConstantsByName("TYPO3_version")
                .iterator();

        if (!typo3_version.hasNext()) {
            return null;
        }

        return typo3_version.next();
    }

    @Nullable
    public static Constant getBranchVersionConstant(@NotNull Project project) {
        Iterator<Constant> typo3_branch = PhpIndex
                .getInstance(project)
                .getConstantsByName("TYPO3_branch")
                .iterator();

        if (!typo3_branch.hasNext()) {
            return null;
        }

        return typo3_branch.next();
    }

    @Nullable
    public static String getTYPO3Branch(@NotNull Project project) {
        Constant versionConstant = getBranchVersionConstant(project);
        if (versionConstant == null) {
            return null;
        }

        PsiElement value = versionConstant.getValue();
        if (value instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) value).getContents();
        }

        return null;
    }

    @Nullable
    public static String getTYPO3Version(@NotNull Project project) {
        Constant versionConstant = getVersionConstant(project);
        if (versionConstant == null) {
            return null;
        }

        PsiElement value = versionConstant.getValue();
        if (value instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) value).getContents();
        }

        return null;
    }

    /**
     * Examples: 1.0rc1 < 1.0release, 1.0 < 1.0.1, 1.1 > 1.02
     *
     * @return 0 if cms version equals given version, positive value if cms version > given version, negative value if cms version < given version
     */
    public static Integer compareVersion(@NotNull Project project, String version) {
        String typo3Version = getTYPO3Version(project);

        return VersionComparatorUtil.compare(typo3Version, version);
    }

    /**
     * Examples: 1.0rc1 < 1.0release, 1.0 < 1.0.1, 1.1 > 1.02
     *
     * @return 0 if cms version equals given version, positive value if cms version > given version, negative value if cms version < given version
     */
    public static Integer compareMajorMinorVersion(@NotNull Project project, String version) {
        String typo3Version = getTYPO3Branch(project);

        return VersionComparatorUtil.compare(typo3Version, version);
    }

    public static boolean isMajorMinorCmsVersion(@NotNull Project project, String version) {
        return compareMajorMinorVersion(project, version).equals(0);
    }

    @Nullable
    public static String getFQNByAspectName(@NotNull String aspectName) {
        if (DEFAULT_ASPECTS.containsKey(aspectName)) {

            return DEFAULT_ASPECTS.get(aspectName);
        }

        return null;
    }

    public static Set<String> getAvailableAspects() {

        return DEFAULT_ASPECTS.keySet();
    }
}
