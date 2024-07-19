package net.serenitybdd.core.photography.bluring;

import com.jhlabs.image.BoxBlurFilter;
import net.serenitybdd.core.photography.AmendedPathBuilder;
import net.serenitybdd.core.photography.PhotoFilter;
import net.serenitybdd.core.photography.ScreenshotNegative;
import net.serenitybdd.annotations.BlurLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.newInputStream;

public class Blurer implements PhotoFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public Path amendedScreenshotPath(ScreenshotNegative negative) {

        if (negative.getBlurLevel() == BlurLevel.NONE) {
            return negative.getScreenshotPath();
        }

        return amendScreenshotPathFor(negative).withPrefix("BLURRED_" + negative.getBlurLevel() + "_");
    }


    private AmendedPathBuilder amendScreenshotPathFor(ScreenshotNegative negative) {
        return new AmendedPathBuilder(negative);
    }

    @Override
    public ScreenshotNegative process(ScreenshotNegative negative) {

        ScreenshotNegative amendedNegative = negative.withScreenshotPath(amendedScreenshotPath(negative));

        if (negative.getBlurLevel() == BlurLevel.NONE) {
            return amendedNegative;
        }

        try (
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            InputStream imageStream = newInputStream(amendedNegative.getTemporaryPath())
        ) {
            BufferedImage srcImage = ImageIO.read(imageStream);
            BufferedImage destImage = deepCopy(srcImage);
            destImage = withFilterFor(negative.getBlurLevel()).filter(srcImage, destImage);

            ImageIO.write(destImage, "png", outStream);
            Files.write(negative.getTemporaryPath(), outStream.toByteArray());
        } catch (Throwable e) {
            LOGGER.warn("Failed to blur screenshot", e);
        }
        return amendedNegative;
    }

    private BoxBlurFilter withFilterFor(BlurLevel blurLevel) {
        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(blurLevel.getRadius());
        boxBlurFilter.setIterations(3);
        return boxBlurFilter;
    }

    private BufferedImage deepCopy(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
