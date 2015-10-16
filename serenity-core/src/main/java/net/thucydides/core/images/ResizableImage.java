package net.thucydides.core.images;

import net.thucydides.core.screenshots.ScreenshotException;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

public class ResizableImage {

    private final File screenshotFile;
    private final int MAX_SUPPORTED_HEIGHT = 4000;

    private Dimension dimension;

    private final Logger logger = LoggerFactory.getLogger(ResizableImage.class);

    protected Logger getLogger() {
        return logger;
    }

    public ResizableImage(final File screenshotFile) throws IOException {
        this.screenshotFile = screenshotFile;
    }

    public static ResizableImage loadFrom(final File screenshotFile) throws IOException {
        return new ResizableImage(screenshotFile);
    }

    public int getWidth() {
        return getImageDimension().width;
    }

    private Dimension getImageDimension() {
        if (dimension != null) {
            return dimension;
        }

        String suffix = this.getFileSuffix(screenshotFile.getPath());
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersBySuffix(suffix);
        if (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
            try {
                ImageInputStream stream = new FileImageInputStream(screenshotFile);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                dimension = new Dimension(width, height);
            } catch (IOException e) {
                logger.warn("Could not find the dimensions of the screenshot for " + screenshotFile);
                return new Dimension(0,0);
            } finally {
                reader.dispose();
            }
        } else {
            throw new ScreenshotException("Could not find the dimensions of the screenshot for " + screenshotFile);
        }
        return dimension;
    }

    private String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }

    public int getHeight() {
        return getImageDimension().height;
    }

    public ResizableImage rescaleCanvas(final int height) throws IOException {

        if (skipRescale(height)) {
            return this;
        }

        int targetHeight = Math.min(height, MAX_SUPPORTED_HEIGHT);

        try {
            waitForCreationOfFile();
            return resizeImage(getWidth(), targetHeight, ImageIO.read(screenshotFile));
        } catch (Throwable e) {
            getLogger().warn("Could not resize screenshot, so leaving original version: " + screenshotFile, e);
            return this;
        }
    }

    private void waitForCreationOfFile() {
        await().atMost(30, TimeUnit.SECONDS).until(screenshotIsProcessed());
    }

    private Callable<Boolean> screenshotIsProcessed() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return (screenshotFile.exists() && screenshotFile.length() > 0);
            }
        };
    }

    protected ResizableImage resizeImage(int width, int targetHeight, BufferedImage image) throws IOException {
        BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.SPEED,Scalr.Mode.FIT_TO_WIDTH, width, targetHeight, Scalr.OP_ANTIALIAS);
	    return new ResizedImage(resizedImage, screenshotFile);
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

    /**
     * If no resize operation has been done, just copy the file.
     * Otherwise we should be applying the saveTo() method on the ResizedImage class.
     */
    public void saveTo(final File savedFile) throws IOException {
        Files.copy(screenshotFile.toPath(), savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
