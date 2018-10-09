package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.TYPO3CMSProjectComponent;
import com.cedricziel.idea.typo3.TYPO3CMSProjectSettings;
import com.cedricziel.idea.typo3.TYPO3Patterns;
import com.cedricziel.idea.typo3.icons.IconStub;
import com.cedricziel.idea.typo3.index.IconIndex;
import com.cedricziel.idea.typo3.util.IconUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;

import static com.cedricziel.idea.typo3.util.IconUtil.createIconFromFile;

public class IconLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {

        if (!TYPO3CMSProjectSettings.isEnabled(element)) {
            return;
        }

        if (TYPO3CMSProjectSettings.getInstance(element).iconUsageGutterIconsEnabled) {
            if (TYPO3Patterns.iconAPIIconRetrieval(element)) {
                renderUsageMarker(element, result);
            }
        }

        if (TYPO3CMSProjectSettings.getInstance(element).iconDefinitionGutterIconsEnabled) {
            renderDefinitionMarker(element, result);
        }
    }

    private void renderUsageMarker(PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        StringLiteralExpression literalExpression = (StringLiteralExpression) element.getParent();
        String value = literalExpression.getContents();

        if (value.isEmpty()) {
            return;
        }

        if (!IconIndex.hasIcon(element.getProject(), value)) {
            return;
        }

        IconIndex.getIcon(element.getProject(), value).forEach((s) -> markLineForIcon(element, result, s));
    }

    private void renderDefinitionMarker(PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        PhpClass parentClass = PsiTreeUtil.getParentOfType(element, PhpClass.class);
        if (parentClass == null) {
            return;
        }

        if (!parentClass.getPresentableFQN().equals(IconUtil.ICON_REGISTRY_CLASS)) {
            return;
        }

        for (IconStub icon : IconIndex.getAllIcons(element.getProject())) {
            if (icon.getElement().equals(element)) {
                if (!(element instanceof LeafPsiElement)) {
                    LeafPsiElement leafChild = PsiTreeUtil.findChildOfType(element, LeafPsiElement.class);
                    if (leafChild != null) {
                        markLineForIcon(leafChild, result, icon);
                    }
                } else {
                    markLineForIcon(element, result, icon);
                }
            }
        }
    }

    private void markLineForIcon(PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result, IconStub iconForLine) {
        NavigationGutterIconBuilder<PsiElement> builder;
        VirtualFile virtualFile = iconForLine.getElement().getContainingFile().getVirtualFile();
        if (virtualFile == null) {
            builder = NavigationGutterIconBuilder
                .create(TYPO3CMSIcons.ICON_NOT_RESOLVED)
                .setTarget(iconForLine.getElement())
                .setTooltipText("Navigate to icon definition");
        } else {
            try {
                Icon icon = createIconFromFile(virtualFile);
                if (icon == null) {
                    icon = TYPO3CMSIcons.ICON_NOT_RESOLVED;
                }

                builder = NavigationGutterIconBuilder
                    .create(icon)
                    .setTarget(iconForLine.getElement())
                    .setTooltipTitle("Navigate to icon definition");
            } catch (IOException e) {
                // icon could not be loaded
                TYPO3CMSProjectComponent.getLogger().error("Could not find image.");
                return;
            }
        }

        result.add(builder.createLineMarkerInfo(element));
    }
}
