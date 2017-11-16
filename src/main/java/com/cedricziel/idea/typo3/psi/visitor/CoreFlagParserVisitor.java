package com.cedricziel.idea.typo3.psi.visitor;

import com.cedricziel.idea.typo3.domain.IconStub;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;

import java.util.*;

/**
 * @author Cedric Ziel <cedric@cedric-ziel.com>
 */
public class CoreFlagParserVisitor extends PsiElementVisitor {

    private final Map<String, IconStub> map;

    public CoreFlagParserVisitor() {
        this.map = new HashMap<>();
    }

    public Map<String, IconStub> getMap() {
        return map;
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

        IconStub icon = new IconStub(iconIdentifier, element);
        icon.setSource(iconSource);
        icon.setProvider(iconProvider.getFQN());

        map.put(iconIdentifier, icon);
    }
}
