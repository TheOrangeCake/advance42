package swingy.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class FileLoader {
    public static ImageIcon loadScaledImage(String resourcePath, int maxW, int maxH) {
        try {
            URL url = FileLoader.class.getResource(resourcePath);
            if (url == null) return null;
            Image img = ImageIO.read(url).getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
