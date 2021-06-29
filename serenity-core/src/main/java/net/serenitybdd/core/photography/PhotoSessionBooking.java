package net.serenitybdd.core.photography;

import com.google.common.base.Preconditions;
import net.thucydides.core.screenshots.BlurLevel;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;

public class PhotoSessionBooking {

    private final Darkroom darkroom;
    private WebDriver driver;
    private Path outputDirectory;
    private BlurLevel blurLevel;

    public PhotoSessionBooking(Darkroom darkroom) {
        this.darkroom = darkroom;

    }

    public PhotoSessionBooking with(WebDriver driver) {
        this.driver = driver;
        return this;
    }

    public PhotoSessionBooking andWithBlurring(BlurLevel blurLevel) {
        this.blurLevel = blurLevel;
        return this;
    }

    public ScreenshotPhoto andSaveToDirectory(Path outputDirectory) {
        Preconditions.checkNotNull(driver);
        Preconditions.checkNotNull(outputDirectory);

        this.outputDirectory = outputDirectory;

        return inPhotoSession().takeScreenshot();
    }

    private PhotoSession inPhotoSession() {
        return new PhotoSession(driver, darkroom, outputDirectory, blurLevel);
    }

}
