package com.cedricziel.idea.typo3.util;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IconUtil {

    public static Icon createIconFromFile(VirtualFile virtualFile) throws IOException {
        Image image;
        if (virtualFile.getExtension().equalsIgnoreCase("svg")) {
            try {
                image = IconUtil.svg2png(new File(virtualFile.getPath()));
            } catch (TranscoderException e) {
                // invalid svg
                return null;
            } catch (RuntimeException e) {
                // unsupported svg version
                return null;
            }
        } else {
            image = ImageIO.read(virtualFile.getInputStream());
        }
        if (image == null) {
            return null;
        }
        Icon icon = IconLoader.getIcon(image);

        if (UIUtil.isRetina()) {
            icon = scaleImage((ImageIcon) icon, 32, 32);
        } else {
            icon = scaleImage((ImageIcon) icon, 16, 16);
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

    public static BufferedImage svg2png(File svgFile) throws IOException, TranscoderException {
        MyTranscoder transcoder = new MyTranscoder();
        transcoder.transcode(new TranscoderInput(svgFile.toURL().toString()), null);

        return transcoder.getImage();
    }

    private static class MyTranscoder extends ImageTranscoder {
        private BufferedImage image = null;
        public BufferedImage createImage(int w, int h) {
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            return image;
        }
        public void writeImage(BufferedImage img, TranscoderOutput out) {
        }
        public BufferedImage getImage() {
            return image;
        }
    }
}
