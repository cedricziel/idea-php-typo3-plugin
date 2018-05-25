package com.cedricziel.idea.typo3.util;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ExtbaseUtility {
    public static String convertRepositoryFQNToEntityFQN(@NotNull String repositoryClassFQN) {
        return StringUtils.stripStart(StringUtils.replace(StringUtils.replace(repositoryClassFQN, "\\Domain\\Repository", "\\Domain\\Model"), "Repository", ""), "\\");
    }
}
