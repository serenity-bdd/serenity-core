package net.thucydides.core.screenshots;

import com.google.common.base.Objects;

import java.io.File;
import java.util.Optional;

/**
 * A screenshot image and the corresponding HTML source code.
 */
public class ScreenshotAndHtmlSource {

    private final File screenshot;
    private final File htmlSource;

    public ScreenshotAndHtmlSource(String screenshotName, String sourcecodeName) {
        this.screenshot = new File(screenshotName);
        this.htmlSource = (sourcecodeName != null) ? new File(sourcecodeName) : null;
    }

    public ScreenshotAndHtmlSource(File screenshot, File sourcecode) {
        this.screenshot = screenshot;
        this.htmlSource = sourcecode;
    }

    public String getScreenshotName() {
        return screenshot.getName();
    }

    public String getHtmlSourceName() {
        if (htmlSource == null) {
            return null;
        }
        return htmlSource.getName();
    }

    public ScreenshotAndHtmlSource(File screenshot) {
        this(screenshot, null);
    }

    public File getScreenshot() {
        return screenshot;
    }

    public Optional<File> getHtmlSource() {
        return Optional.ofNullable(htmlSource);
    }

    public boolean wasTaken() {
        return (screenshot != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenshotAndHtmlSource that = (ScreenshotAndHtmlSource) o;
        return Objects.equal(screenshot, that.screenshot) &&
                Objects.equal(htmlSource, that.htmlSource);
    }

    @Override
    public int hashCode() {
        return screenshot != null ? screenshot.hashCode() : 0;
    }

    public boolean hasIdenticalScreenshotsAs(ScreenshotAndHtmlSource anotherScreenshotAndHtmlSource) {
        if (hasNoScreenshot() || anotherScreenshotAndHtmlSource.hasNoScreenshot()) {
            return false;
        }
        return (getScreenshot().getName().equals(anotherScreenshotAndHtmlSource.getScreenshot().getName()));
    }

    public File getScreenshotFile(File screenshotTargetDirectory) {
        return new File(screenshotTargetDirectory, getScreenshot().getName());
    }
    public boolean hasNoScreenshot() {
        return getScreenshot() == null;
    }
}
