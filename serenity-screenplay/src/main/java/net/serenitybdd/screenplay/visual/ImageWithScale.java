package net.serenitybdd.screenplay.visual;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A screenshot image with its device pixel ratio scale factor.
 *
 * <p>The scale factor represents the ratio between physical pixels and logical (CSS) pixels.
 * For standard displays this is 1.0, for Retina/HiDPI displays it is typically 2.0 or higher.</p>
 */
public class ImageWithScale {

    private final BufferedImage image;
    private final double scale;

    public ImageWithScale(byte[] bytes, double scale) {
        try {
            this.image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read screenshot image", e);
        }
        this.scale = scale;
    }

    public ImageWithScale(BufferedImage image, double scale) {
        this.image = image;
        this.scale = scale;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getScale() {
        return scale;
    }

    public String description() {
        String desc = image.getWidth() + "x" + image.getHeight();
        if (scale != 1.0) {
            desc += "@" + scale;
        }
        return desc;
    }
}
