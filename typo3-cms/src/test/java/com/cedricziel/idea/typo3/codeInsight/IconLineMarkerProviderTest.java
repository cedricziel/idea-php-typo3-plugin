package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class IconLineMarkerProviderTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/codeInsight/icon";
    }

    public void testLineMarkerIsNotAddedIfPluginIsNotEnabled() {
        disablePlugin();

        myFixture.copyFileToProject("IconRegistry7.php");

        PsiFile psiFile = myFixture.configureByFile("icon_provider_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarkerIsEmpty(elementAt);
    }

    public void testLineMarkerIsNotAddedIfPluginIsEnabledButFeatureTurnedOff() {
        TYPO3CMSProjectSettings.getInstance(getProject()).iconUsageGutterIconsEnabled = false;

        myFixture.copyFileToProject("IconRegistry7.php");

        PsiFile psiFile = myFixture.configureByFile("icon_provider_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarkerIsEmpty(elementAt);
    }

    public void testLineMarkerIsAddedIfPluginIsEnabled() {
        myFixture.copyFileToProject("IconRegistry7.php");

        PsiFile psiFile = myFixture.configureByFile("icon_provider_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarker(elementAt, "Navigate to icon definition");
    }

    public void testDefinitionLineMarkerIsNotAddedIfPluginIsNotEnabled() {
        disablePlugin();

        PsiFile psiFile = myFixture.configureByFile("icon_definition_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarkerIsEmpty(elementAt);
    }

    public void testDefinitionLineMarkerIsNotAddedIfPluginIsEnabledButFeatureTurnedOff() {
        TYPO3CMSProjectSettings.getInstance(getProject()).iconDefinitionGutterIconsEnabled = false;

        PsiFile psiFile = myFixture.configureByFile("icon_definition_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarkerIsEmpty(elementAt);
    }

    public void testDefinitionLineMarkerIsAddedIfPluginIsEnabled() {
        PsiFile psiFile = myFixture.configureByFile("icon_definition_test.php");

        PsiElement elementAt = psiFile.findElementAt(myFixture.getCaretOffset());
        assertLineMarker(elementAt, "Navigate to icon definition");
    }
}
