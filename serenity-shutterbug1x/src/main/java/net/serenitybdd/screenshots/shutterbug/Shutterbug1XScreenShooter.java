package net.serenitybdd.screenshots.shutterbug;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.PageSnapshot;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import net.serenitybdd.core.photography.PhotoLens;
import net.serenitybdd.core.photography.ScreenShooter;
import net.serenitybdd.core.photography.WebDriverPhotoLens;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_FULL_PAGE_SCREENSHOT_STRATEGY;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_SCREENSHOT_STRATEGY;

/**
 * Takes screenshots using Shutterbug 1.5
 * You can configure Shutterbug with the following properties:
 * <ul>
 *     <li>shutterbug.capturestrategy (VIEWPORT, FULL, FULL_SCROLL, VERTICAL_SCROLL, HORIZONTAL_SCROLL)</li>
 *     <li>shutterbug.betweenScrollTimeout – Timeout to wait between each scrolling operation</li>
 *     <li>shutterbug.useDevicePixelRatio – whether or not take into account device pixel ratio</li>
 *  </ul>
 * @Deprecated and replaced by the Shutterbug 2 integration.
 */

public class Shutterbug1XScreenShooter implements ScreenShooter {
    private final WebDriver driver;
    private final EnvironmentVariables environmentVariables;

    public Shutterbug1XScreenShooter(PhotoLens lens) {
        this.driver = ((WebDriverPhotoLens) lens).getDriver();
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Override
    public byte[] takeScreenshot() throws IOException {
        int betweenScrollTimeout = Integer.parseInt(
                environmentVariables.getProperty("shutterbug.betweenScrollTimeout","100")
        );
        boolean useDevicePixelRatio = Boolean.parseBoolean(
                environmentVariables.getProperty("shutterbug.useDevicePixelRatio","true")
        );
        PageSnapshot snapshot = Shutterbug.shootPage(driver, captureStrategy(), betweenScrollTimeout, useDevicePixelRatio);
        return asByteArray(snapshot.getImage());
    }

    private Capture captureStrategy() {
        if (environmentVariables.aValueIsDefinedFor("shutterbug.capturestrategy")) {
            return Capture.valueOf(environmentVariables.getProperty("shutterbug.capturestrategy", "VIEWPORT"));
        } else if (environmentVariables.aValueIsDefinedFor(SERENITY_SCREENSHOT_STRATEGY)) {
            return Capture.valueOf(environmentVariables.getValue(SERENITY_SCREENSHOT_STRATEGY));
        } else if (environmentVariables.aValueIsDefinedFor(SERENITY_FULL_PAGE_SCREENSHOT_STRATEGY)) {
            return (SERENITY_FULL_PAGE_SCREENSHOT_STRATEGY.booleanFrom(environmentVariables) ? Capture.FULL_SCROLL : Capture.VIEWPORT);
        } else {
            return Capture.VIEWPORT;
        }
    }

    private byte[] asByteArray(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }
    }
}
