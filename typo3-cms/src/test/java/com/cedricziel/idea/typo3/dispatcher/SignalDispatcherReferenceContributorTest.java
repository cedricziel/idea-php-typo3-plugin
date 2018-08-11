package com.cedricziel.idea.typo3.dispatcher;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;

import java.util.List;

public class SignalDispatcherReferenceContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/dispatcher";
    }

    public void testReferenceCanBeResolvedWithClassConstantReference() {
        myFixture.copyFileToProject("classes.php");

        configureSignalSlotConnect(
            "\\TYPO3\\CMS\\Core\\Core\\ClassLoadingInformation::class",
            "'dumpClassLoadingInformat<caret>ion'"
        );

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertNotNull(elementAtCaret);
        assertInstanceOf(elementAtCaret, Method.class);
        assertEquals("dumpClassLoadingInformation", ((Method) elementAtCaret).getName());
    }

    public void testReferenceCanBeResolvedWithStringClassReference() {
        myFixture.copyFileToProject("classes.php");

        configureSignalSlotConnect(
            "'\\TYPO3\\CMS\\Core\\Core\\ClassLoadingInformation'",
            "'dumpClassLoadingInformat<caret>ion'"
        );

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertNotNull(elementAtCaret);
        assertInstanceOf(elementAtCaret, Method.class);
        assertEquals("dumpClassLoadingInformation", ((Method) elementAtCaret).getName());
    }

    private void configureSignalSlotConnect(String classParameters, String methodParameter) {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var \\TYPO3\\CMS\\Extbase\\SignalSlot\\Dispatcher $signalSlotDispatcher */\n" +
            "$signalSlotDispatcher = \\TYPO3\\CMS\\Core\\Utility\\GeneralUtility::makeInstance(\\TYPO3\\CMS\\Extbase\\SignalSlot\\Dispatcher::class);\n" +
            "$signalSlotDispatcher->connect(\n" +
            "    \\TYPO3\\CMS\\Extensionmanager\\Utility\\InstallUtility::class,  // Signal class name\n" +
            "    'foo',                                                           // Signal name\n" +
            "    " + classParameters + ",                                         // Slot class name\n" +
            "    " + methodParameter + "                                          // Slot name\n" +
            ");"
        );
    }

    public void testCanProvideVariantsWithClassConstantReference() {
        myFixture.copyFileToProject("classes.php");

        configureSignalSlotConnect("\\TYPO3\\CMS\\Core\\Core\\ClassLoadingInformation::class", "'<caret>'");

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue(lookupElementStrings.contains("dumpClassLoadingInformation"));
        assertTrue(lookupElementStrings.contains("anotherOne"));
        assertFalse(lookupElementStrings.contains("aPrivateOne"));
        assertFalse(lookupElementStrings.contains("aProtectedOne"));
    }

    public void testCanProvideVariantsWithStringClassReference() {
        myFixture.copyFileToProject("classes.php");

        configureSignalSlotConnect("'\\TYPO3\\CMS\\Core\\Core\\ClassLoadingInformation'", "'<caret>'");

        myFixture.completeBasic();

        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertTrue(lookupElementStrings.contains("dumpClassLoadingInformation"));
        assertTrue(lookupElementStrings.contains("anotherOne"));
        assertFalse(lookupElementStrings.contains("aPrivateOne"));
        assertFalse(lookupElementStrings.contains("aProtectedOne"));
    }
}
