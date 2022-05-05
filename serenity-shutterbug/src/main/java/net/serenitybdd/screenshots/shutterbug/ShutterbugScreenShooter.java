package net.serenitybdd.screenshots.shutterbug;

import com.assertthat.selenium_shutterbug.core.PageSnapshot;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
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

/**
 * Takes screenshots using Shutterbug 0.9.5
 * You can configure Shutterbug with the following properties:
 * <ul>
 *     <li>shutterbug.scrollstrategy (VIEWPORT_ONLY,WHOLE_PAGE or WHOLE_PAGE_SCROLL_AND_STITCH)</li>
 *     <li>shutterbug.betweenScrollTimeout – Timeout to wait between each scrolling operation</li>
 *     <li>shutterbug.useDevicePixelRatio – whether or not take into account device pixel ratio</li>
 *  </ul>
 */
public class ShutterbugScreenShooter implements ScreenShooter {
    private final WebDriver driver;
    private final EnvironmentVariables environmentVariables;

    public ShutterbugScreenShooter(PhotoLens lens) {
        this.driver = ((WebDriverPhotoLens) lens).getDriver();
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Override
    public byte[] takeScreenshot() throws IOException {
        ScrollStrategy scrollStrategy = ScrollStrategy.valueOf(
                environmentVariables.getValue("shutterbug.scrollstrategy","WHOLE_PAGE")
        );
        int betweenScrollTimeout = Integer.parseInt(
                environmentVariables.getValue("shutterbug.betweenScrollTimeout","100")
        );
        boolean useDevicePixelRatio = Boolean.parseBoolean(
                environmentVariables.getValue("shutterbug.useDevicePixelRatio","true")
        );
        PageSnapshot snapshot = Shutterbug.shootPage(driver, scrollStrategy, betweenScrollTimeout, useDevicePixelRatio);
        return asByteArray(snapshot.getImage());
    }

    private byte[] asByteArray(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }
    }
}
