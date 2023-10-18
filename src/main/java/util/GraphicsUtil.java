package util;

import constants.UIConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class GraphicsUtil {

    /**
     * Creates a JPanel with a colored square and a label in the center.
     * @param color The color of the square.
     * @param size The size of the square.
     * @param label The label to be displayed in the center of the square.
     * @return JPanel containing the colored square with the label in the center.
     */
    public static JPanel createColoredSquareWithLabel(Color color, int size, String label) {
        JPanel squarePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(color);
                RoundRectangle2D.Double roundedSquare = new RoundRectangle2D.Double((double) (UIConstants.COURSE_GUI_FRAME_SIZE[0] / 2) - 70, 0, size, size, 5, 5);
                g2d.fill(roundedSquare);
            }
        };
        squarePanel.setLayout(new BorderLayout());

        JLabel labelComponent = new JLabel(label);
        labelComponent.setHorizontalAlignment(SwingConstants.CENTER);
        squarePanel.add(labelComponent, BorderLayout.CENTER);

        return squarePanel;
    }

    /**
     * Retrieves pixels from the specified BufferedImage.
     * @param img The BufferedImage from which pixels are to be retrieved.
     * @param x The x-coordinate of the upper-left corner of the specified rectangular region.
     * @param y The y-coordinate of the upper-left corner of the specified rectangular region.
     * @param w The width of the specified rectangular region.
     * @param h The height of the specified rectangular region.
     * @param pixels The array where the pixels will be stored.
     * @return An array of pixels from the BufferedImage.
     */
    public static int[] getPixels(BufferedImage img,
            int x, int y, int w, int h, int[] pixels) {
        if (w == 0 || h == 0) {
            return new int[0];
        }

        if (pixels == null) {
            pixels = new int[w * h];
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length"
                    + " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB
                || imageType == BufferedImage.TYPE_INT_RGB) {
            Raster raster = img.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        // Unmanages the image
        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    /**
     * Sets pixels to the specified BufferedImage.
     * @param img The BufferedImage to which pixels are to be set.
     * @param x The x-coordinate of the upper-left corner of the specified rectangular region.
     * @param y The y-coordinate of the upper-left corner of the specified rectangular region.
     * @param w The width of the specified rectangular region.
     * @param h The height of the specified rectangular region.
     * @param pixels The array of pixels to be set.
     */
    public static void setPixels(BufferedImage img,
            int x, int y, int w, int h, int[] pixels) {
        if (pixels == null || w == 0 || h == 0) {
            return;
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length"
                    + " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB
                || imageType == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = img.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        } else {
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }
}
