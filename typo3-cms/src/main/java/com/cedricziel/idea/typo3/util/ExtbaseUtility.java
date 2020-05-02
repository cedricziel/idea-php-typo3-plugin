package com.cedricziel.idea.typo3.util;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExtbaseUtility {

    public static final String CONTROLLER_BASE_CLASS = "\\TYPO3\\CMS\\Extbase\\Mvc\\Controller\\ActionController";

    public static String convertRepositoryFQNToEntityFQN(@NotNull String repositoryClassFQN) {
        return StringUtils.stripStart(StringUtils.replace(StringUtils.replace(repositoryClassFQN, "\\Domain\\Repository", "\\Domain\\Model"), "Repository", ""), "\\");
    }

    public static boolean isActionController(@NotNull PhpClass containingClass) {
        List<String> fqns = new ArrayList<>();

        if (containingClass.getSuperFQN() != null) {
            fqns.add(containingClass.getSuperFQN());
        }

        return fqns.contains(CONTROLLER_BASE_CLASS);
    }
}
