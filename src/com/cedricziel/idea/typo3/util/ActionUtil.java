package com.cedricziel.idea.typo3.util;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiDirectory;

public class ActionUtil {
    /**
     * Finds the directories on which an action was performed on.
     *
     * @param actionEvent The source action event
     * @return an array of directories the action was performed on
     */
    public static PsiDirectory[] findDirectoryFromActionEvent(AnActionEvent actionEvent) {

        DataContext dataContext = actionEvent.getDataContext();
        IdeView data = LangDataKeys.IDE_VIEW.getData(dataContext);

        if (data == null) {
            return new PsiDirectory[]{};
        }

        return data.getDirectories();
    }
}
