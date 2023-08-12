package net.thucydides.model.images;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResizedImage extends ResizableImage {

    private final BufferedImage image;

    public ResizedImage(final BufferedImage image, final File screenshotFile) throws IOException {
        super(screenshotFile);
        this.image = image;
    }

    @Override
    public void saveTo(final File file) throws IOException {
        ImageIO.write(image, "PNG", file);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }
}
