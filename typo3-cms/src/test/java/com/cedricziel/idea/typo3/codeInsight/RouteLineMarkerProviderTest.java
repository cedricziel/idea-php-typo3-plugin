package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class RouteLineMarkerProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInsight";
    }

    public void testLineMarkerIsNotAddedIfPluginIsNotEnabled() {
        disablePlugin();

        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("Routes.php");

        PsiFile psiFile = myFixture.configureByFile("route_linemarker_not_enabled.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarkerIsEmpty(elementAt);
    }

    public void testLineMarkerIsAddedIfPluginIsEnabled() {
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("Routes.php");

        PsiFile psiFile = myFixture.configureByFile("route_linemarker_enabled.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarker(elementAt, "Path: /record/importexport/");
    }
}
