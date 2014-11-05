package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A screenshot image and the corresponding HTML source code.
 */
public class ScreenshotAndHtmlSource {

    private final File screenshotFile;
    private final File htmlSource;

    public ScreenshotAndHtmlSource(String screenshotName, String sourcecodeName) {
        this.screenshotFile = new File(screenshotName);
        this.htmlSource = (sourcecodeName != null) ? new File(sourcecodeName) : null;
    }

    public ScreenshotAndHtmlSource(File screenshotFile, File sourcecode) {
        this.screenshotFile = screenshotFile;
        this.htmlSource = sourcecode;
    }

    public String getScreenshotName() {
        return screenshotFile.getName();
    }

    public String getHtmlSourceName() {
        if (htmlSource == null) {
            return null;
        }
        return htmlSource.getName();
    }

    public ScreenshotAndHtmlSource(File screenshotFile) {
        this(screenshotFile, null);
    }

    public File getScreenshotFile() {
        return screenshotFile;
    }

    public Optional<File> getHtmlSource() {
        return Optional.fromNullable(htmlSource);
    }

    public boolean wasTaken() {
        return (screenshotFile != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenshotAndHtmlSource)) return false;

        ScreenshotAndHtmlSource that = (ScreenshotAndHtmlSource) o;

        if (screenshotFile == null) {
            return (that.screenshotFile == null);
        } else if (that.screenshotFile == null) {
            return (this.screenshotFile == null);
        } else {
            try {
                return FileUtils.contentEquals(screenshotFile, that.screenshotFile);
            } catch (IOException e) {
                return false;
            }
        }
    }


    @Override
    public int hashCode() {
        return screenshotFile != null ? screenshotFile.hashCode() : 0;
    }

    public boolean hasIdenticalScreenshotsAs(ScreenshotAndHtmlSource anotherScreenshotAndHtmlSource) {
        if (hasNoScreenshot() || anotherScreenshotAndHtmlSource.hasNoScreenshot()) {
            return false;
        }
        return (getScreenshotFile().getName().equals(anotherScreenshotAndHtmlSource.getScreenshotFile().getName()));
    }

    public File getScreenshotFile(File screenshotTargetDirectory) {
        return new File(screenshotTargetDirectory, getScreenshotFile().getName());
    }
    public boolean hasNoScreenshot() {
        return getScreenshotFile() == null;
    }
}
