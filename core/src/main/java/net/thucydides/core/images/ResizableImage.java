package net.thucydides.core.images;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResizableImage {

    private final File screenshotFile;
    private final SimpleImageInfo imageInfo;
    private final int MAX_SUPPORTED_HEIGHT = 4000;

    private final Logger logger = LoggerFactory.getLogger(ResizableImage.class);

    protected Logger getLogger() {
        return logger;
    }

    public ResizableImage(final File screenshotFile) throws IOException {
        this.screenshotFile = screenshotFile;
        this.imageInfo = new SimpleImageInfo(screenshotFile);
    }

    public static ResizableImage loadFrom(final File screenshotFile) throws IOException {
        return new ResizableImage(screenshotFile);
    }

    public int getWitdh() {
        return imageInfo.getWidth();
    }

    public int getHeight() {
        return imageInfo.getHeight();
    }

    public ResizableImage rescaleCanvas(final int height) throws IOException {

        if (skipRescale(height)) {
            return this;
        }

        int targetHeight = Math.min(height, MAX_SUPPORTED_HEIGHT);

        BufferedImage image = ImageIO.read(screenshotFile);
        int width = new SimpleImageInfo(screenshotFile).getWidth();
        try {
            return resizeImage(width, targetHeight, image);
        } catch (IllegalArgumentException e) {
            getLogger().warn("Could not resize screenshot: " + screenshotFile, e);
            return this;
        }
    }

    protected ResizableImage resizeImage(int width, int targetHeight, BufferedImage image) throws IOException {
        try {
            int imageType = (image.getType() > 0) ? image.getType() : BufferedImage.TYPE_4BYTE_ABGR;
			BufferedImage resizedImage = new BufferedImage(width, targetHeight, imageType);
			fillWithWhiteBackground(resizedImage);
			resizedImage.setData(image.getRaster());
	        return new ResizedImage(resizedImage, screenshotFile);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
    }

    private boolean skipRescale(int height) {
        if (getHeight() > MAX_SUPPORTED_HEIGHT) {
            return true;
        }

        if (getHeight() >= height) {
            return true;
        }

        return false;
    }

    private void fillWithWhiteBackground(final BufferedImage resizedImage) {
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle2D.Float(0, 0, resizedImage.getWidth(), resizedImage.getHeight()));
        g2d.dispose();
    }

    /**
     * If no resize operation has been done, just copy the file.
     * Otherwise we should be applying the saveTo() method on the ResizedImage class.
     */
    public void saveTo(final File savedFile) throws IOException {
        FileUtils.copyFile(screenshotFile, savedFile);
    }
}
