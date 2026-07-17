package net.serenitybdd.screenplay.visual;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;

/**
 * A screenshot image with its device pixel ratio scale factor.
 *
 * <p>The scale factor represents the ratio between physical pixels and logical (CSS) pixels.
 * For standard displays this is 1.0, for Retina/HiDPI displays it is typically 2.0 or higher.</p>
 */
public class ImageWithScale {

    private final BufferedImage image;
    private final double scale;
    private final String description;
    public static final DecimalFormat scaleFormat = new DecimalFormat("0.##");

    public ImageWithScale(byte[] bytes, double scale) {
        this(readImage(bytes), scale);
    }

    public static BufferedImage readImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read screenshot image", e);
        }
    }

    public ImageWithScale(BufferedImage image, double scale) {
        this(image, scale, makeDescription(image.getWidth(), image.getHeight(), scale));
    }

    public static String makeDescription(int width, int height, double scale) {
        StringBuilder desc = new StringBuilder().append(width).append('x').append(height);
        if (scale != 1.0) {
            desc.append('@').append(scaleFormat.format(scale));
        }
        return desc.toString();
    }

    public ImageWithScale(BufferedImage image, double scale, String description) {
        this.image = image;
        this.scale = scale;
        this.description = description;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getScale() {
        return scale;
    }

    public String getDescription() {
        return description;
    }
}
