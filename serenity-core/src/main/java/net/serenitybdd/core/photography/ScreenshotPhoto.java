package net.serenitybdd.core.photography;

import com.google.common.base.Objects;

import java.nio.file.Path;

/**
 * A screenshot taken by a photographer.
 */
public class ScreenshotPhoto {

    public final static ScreenshotPhoto None = new ScreenshotPhoto(null);

    private final Path pathToScreenshot;

    public ScreenshotPhoto(Path pathToScreenshot) {
        this.pathToScreenshot = pathToScreenshot;
    }

    public Path getPathToScreenshot() {
        return pathToScreenshot;
    }

    public static ScreenshotPhoto forScreenshotAt(Path screenshotPath) {
        return new ScreenshotPhoto(screenshotPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenshotPhoto that = (ScreenshotPhoto) o;
        return Objects.equal(pathToScreenshot, that.pathToScreenshot);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pathToScreenshot);
    }

}
