package net.thucydides.core.screenshots;

import java.io.File;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A screenshot image and the corresponding HTML source code.
 */
public class ScreenshotAndHtmlSource implements Comparable<ScreenshotAndHtmlSource> {

    private final File screenshot;
    private final File htmlSource;
    private Long timeStamp;
    private final String screenshotName;

    public ScreenshotAndHtmlSource(String screenshotName, String sourcecodeName) {
        this.screenshot = new File(screenshotName);
        this.htmlSource = (sourcecodeName != null) ? new File(sourcecodeName) : null;
        this.timeStamp = System.currentTimeMillis();
        this.screenshotName = screenshot.getName();
    }

    public ScreenshotAndHtmlSource(File screenshot, File sourcecode) {
        this.screenshot = screenshot;
        this.htmlSource = sourcecode;
        this.timeStamp = System.currentTimeMillis();
        this.screenshotName = (screenshot != null) ? screenshot.getName() : "";
    }

    public String getScreenshotName() {
        return screenshotName;
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
        return Objects.equals(screenshot, that.screenshot) &&
                Objects.equals(htmlSource, that.htmlSource) &&
                Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenshot, htmlSource, timeStamp);
    }

    public Long getTimeStamp() {
        return timeStamp == null ? 0L : timeStamp;
    }

    public String toString() {

        return "Screenshot created at " + Instant.ofEpochMilli(timeStamp) + ": " + screenshot;
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

    @Override
    public int compareTo(ScreenshotAndHtmlSource otherScreenshot) {
        return this.getTimeStamp().compareTo(otherScreenshot.getTimeStamp());
    }
}
