package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class IconUtil {

    public static Icon createIconFromFile(VirtualFile virtualFile) throws IOException {
        Image image = ImageIO.read(virtualFile.getInputStream());
        if (image == null) {
            return null;
        }
        Icon icon = IconLoader.getIcon(image);

        if (UIUtil.isRetina()) {
            icon = scaleImage((ImageIcon) icon, 16, 16);
        } else {
            icon = scaleImage((ImageIcon) icon, 32, 32);
        }
        return icon;
    }

    public static ImageIcon scaleImage(ImageIcon icon, int w, int h) {
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
