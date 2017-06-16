package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Cedric Ziel <cedric@cedric-ziel.com>
 */
public class CoreFlagParserVisitor extends PsiElementVisitor {

    private final Map<String, List<TYPO3IconDefinition>> iconMap;

    public CoreFlagParserVisitor(Map<String, List<TYPO3IconDefinition>> iconMap) {
        this.iconMap = iconMap;
    }

    @Override
    public void visitElement(PsiElement element) {
        Variable iconFolder = PsiTreeUtil.findChildOfType(element, Variable.class);
        if (iconFolder == null || !iconFolder.getName().equals("iconFolder")) {
            // Something changed - dynamic parsing failed.
            return;
        }

        StringLiteralExpression iconFolderValueString = PsiTreeUtil.getNextSiblingOfType(iconFolder, StringLiteralExpression.class);
        if (iconFolderValueString == null) {
            return;
        }

        String iconFolderValue = iconFolderValueString.getContents();

        Statement parentStatement = PsiTreeUtil.getParentOfType(iconFolder, Statement.class);
        Statement iconArrayAssignment = PsiTreeUtil.getNextSiblingOfType(
                parentStatement,
                Statement.class
        );
        if (iconArrayAssignment == null) {
            return;
        }

        Variable iconArray = PsiTreeUtil.findChildOfType(iconArrayAssignment, Variable.class);
        if (iconArray == null) {
            // something changed, meh. dynamic parsing failed.
            return;
        }

        ClassConstantReference iconProvider = PsiTreeUtil.findChildOfType(element, ClassConstantReference.class);
        if (iconProvider == null) {
            // something changed. - ...
            return;
        }

        ArrayCreationExpression flagsArray = PsiTreeUtil.getNextSiblingOfType(iconArray, ArrayCreationExpression.class);
        if (flagsArray == null) {
            return;
        }

        Collection<StringLiteralExpression> flagNames = PsiTreeUtil.findChildrenOfType(flagsArray, StringLiteralExpression.class);
        for (StringLiteralExpression flagNameExpression : flagNames) {
            String countryAbbreviation = flagNameExpression.getContents();

            this.createFlagIcon(iconProvider, iconFolderValue, countryAbbreviation, flagNameExpression);
        }
    }

    private void createFlagIcon(ClassConstantReference iconProvider, String iconFolderValue, String countryAbbreviation, PsiElement element) {
        String prefix = "flags-";
        String iconIdentifier = prefix.concat(countryAbbreviation.toLowerCase());
        String iconSource = iconFolderValue.concat(countryAbbreviation).concat(".svg");

        TYPO3IconDefinition icon = new TYPO3IconDefinition(iconProvider, iconIdentifier, iconSource, element);
        if (iconMap.containsKey(iconIdentifier)) {
            iconMap.get(iconIdentifier).add(icon);

            return;
        }

        List<TYPO3IconDefinition> definitionList = new ArrayList<>();
        definitionList.add(icon);
        iconMap.put(iconIdentifier, definitionList);
    }
}
