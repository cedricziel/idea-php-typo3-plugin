package com.cedricziel.idea.typo3.codeInsight.extbase;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.psi.PhpElementsUtil;
import com.cedricziel.idea.typo3.util.IconUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;

public class EntityLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {

        renderDefinitionMarker(element, result);
    }

    private void renderDefinitionMarker(PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PhpClass)) {
            return;
        }

        PhpClass thisClass = (PhpClass) element;
        Collection<PhpClass> abstractEntityClasses = PhpIndex.getInstance(element.getProject()).getClassesByFQN("\\TYPO3\\CMS\\Extbase\\DomainObject\\AbstractDomainObject");
        if (abstractEntityClasses.isEmpty()) {
            return;
        }

        PhpClass abstractDomainObjectClass = abstractEntityClasses.iterator().next();

        if (!PhpElementsUtil.extendsClass(thisClass, abstractDomainObjectClass)) {
            return;
        }

        markLineForEntityDefinition(element, result);
    }

    private void markLineForEntityDefinition(PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        IconProvider iconProvider = IconProvider.getInstance(element.getProject());

        Icon icon = null;
        if (iconProvider.has("actions-document")) {
            try {
                icon = IconUtil.createIconFromFile(iconProvider.get("actions-document").iterator().next().getVirtualFile());
            } catch (IOException e) {
                icon = TYPO3CMSIcons.ICON_NOT_RESOLVED;
            }
        }

        if (icon == null){
            icon = TYPO3CMSIcons.ICON_NOT_RESOLVED;
        }

        NavigationGutterIconBuilder<PsiElement> builder;
        builder = NavigationGutterIconBuilder
                .create(icon)
                .setTarget(element)
                .setTooltipText("Extbase Entity");

        result.add(builder.createLineMarkerInfo(element));
    }
}
