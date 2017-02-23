package com.cedricziel.idea.typo3.codeInsight;

import com.cedricziel.idea.typo3.TYPO3CMSIcons;
import com.cedricziel.idea.typo3.TYPO3CMSProjectComponent;
import com.cedricziel.idea.typo3.container.IconProvider;
import com.cedricziel.idea.typo3.domain.TYPO3IconDefinition;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class IconLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {

        PhpClass parentClass = PsiTreeUtil.getParentOfType(element, PhpClass.class);
        if (parentClass == null) {
            return;
        }

        String presentableFQN = parentClass.getPresentableFQN();
        if (!presentableFQN.equals(IconProvider.ICON_REGISTRY_CLASS)) {
            return;
        }

        IconProvider iconProvider = IconProvider.getInstance(element.getProject());
        Map<PsiElement, TYPO3IconDefinition> elementMap = iconProvider.getElementMap();

        if (!elementMap.containsKey(element)) {
            return;
        }

        TYPO3IconDefinition iconForLine = elementMap.get(element);
        NavigationGutterIconBuilder<PsiElement> builder;
        VirtualFile virtualFile = iconForLine.getVirtualFile();
        if (virtualFile == null || virtualFile.getExtension().equalsIgnoreCase("svg")) {
            builder = NavigationGutterIconBuilder
                    .create(TYPO3CMSIcons.ROUTE_ICON)
                    .setTarget(iconForLine.getElement())
                    .setTooltipText("Navigate to icon definition");
        } else {
            try {
                Icon icon = createIconFromFile(virtualFile);
                builder = NavigationGutterIconBuilder
                        .create(icon)
                        .setTarget(iconForLine.getElement())
                        .setTooltipText("Navigate to icon definition");
            } catch (IOException e) {
                // icon could not be loaded
                TYPO3CMSProjectComponent.getLogger().error("Could not find image.");
                return;
            }
        }

        result.add(builder.createLineMarkerInfo(element));
    }

    private Icon createIconFromFile(VirtualFile virtualFile) throws IOException {
        Image image = ImageIO.read(virtualFile.getInputStream());
        Icon icon = IconLoader.getIcon(image);

        if(UIUtil.isRetina()) {
            icon = scaleImage((ImageIcon) icon, 16, 16);
        } else {
            icon = scaleImage((ImageIcon) icon, 32, 32);
        }
        return icon;
    }

    private ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if (icon.getIconWidth() > w) {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if (nh > h) {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }
}
