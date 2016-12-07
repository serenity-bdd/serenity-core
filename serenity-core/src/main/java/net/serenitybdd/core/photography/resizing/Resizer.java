package net.serenitybdd.core.photography.resizing;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.photography.PhotoFilter;
import net.serenitybdd.core.photography.ScreenshotNegative;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_WIDTH;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_RESIZED_IMAGE_WIDTH;

public class Resizer implements PhotoFilter {

    private final EnvironmentVariables environmentVariables;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public Resizer() {
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
    }

    @Override
    public Path amendedScreenshotPath(ScreenshotNegative negative) {
        return negative.getScreenshotPath();
    }

    public ScreenshotNegative process(ScreenshotNegative negative) {

        ScreenshotNegative amendedNegative = negative.withScreenshotPath(amendedScreenshotPath(negative));
        try {
            saveResizedScreenshotTo(amendedNegative.getTemporaryPath());
        } catch (IOException e) {
            LOGGER.warn("Could not save resized screenshot", e);
        }
        return amendedNegative;
    }

    private void saveResizedScreenshotTo(Path temporaryPath) throws IOException {
        BufferedImage resizedImage;
        try (InputStream images = Files.newInputStream(temporaryPath)) {
            BufferedImage image = ImageIO.read(images);

            Dimension imageSize = sizeOf(image);
            Dimension targetSize = targetSizeInProportionTo(imageSize);

            if (imageSize.equals(targetSize)) {
                return;
            }
            resizedImage = resize(image, targetSize.width, targetSize.height);
        }
        try (OutputStream resizedImageStream = Files.newOutputStream(temporaryPath)) {
            ImageIO.write(resizedImage, "png", resizedImageStream);
        }
    }

    private Dimension targetSizeInProportionTo(Dimension imageSize) {
        int targetWidth = getResizedWidth();
        int targetHeight = (int) (((double) targetWidth / (double) imageSize.width) * (double) imageSize.height);
        return new Dimension(targetWidth, targetHeight);
    }

    private Dimension sizeOf(BufferedImage image) {
        return new Dimension(image.getData().getWidth(), image.getData().getHeight());
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private int getResizedWidth() {
        return THUCYDIDES_RESIZED_IMAGE_WIDTH.integerFrom(environmentVariables, DEFAULT_WIDTH);
    }

}