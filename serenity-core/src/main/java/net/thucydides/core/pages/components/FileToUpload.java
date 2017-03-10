package net.thucydides.core.pages.components;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.webdriver.ConfigureFileDetector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * A class that helps upload a file to an HTML form in using a fluent API.
 */
public class FileToUpload {
    private final String filename;
    static final String WINDOWS_PATH_PATTERN = "^[A-Z]:\\\\.*";

    private static Pattern fullWindowsPath = Pattern.compile(WINDOWS_PATH_PATTERN);
    private final WebDriver driver;
    private boolean remoteDriver = false;

    public FileToUpload(WebDriver driver, final String filename) {
        this.driver = driver;

        if (isOnTheClasspath(filename)) {
            this.filename = getFileFromResourcePath(filename);
        } else {
            this.filename = getFileFromFileSystem(filename);
        }
    }


    private boolean isOnTheClasspath(final String filename) {
        if (isOnTheUnixFileSystem(filename) || isOnTheWindowsFileSystem(filename)) {
            return false;
        } else {
            return (resourceOnClasspath(filename) != null);
        }
    }

    private URL resourceOnClasspath(final String filename) {
        ClassLoader cldr = Thread.currentThread().getContextClassLoader();
        return cldr.getResource(filename);
    }

    public static boolean isOnTheWindowsFileSystem(final String filename) {
        return (SystemUtils.IS_OS_WINDOWS) && new File(filename).exists();
    }

    public static boolean isAFullWindowsPath(final String filename) {
        return fullWindowsPath.matcher(filename).find();
    }

    public static boolean isOnTheUnixFileSystem(final String filename) {
        return (SystemUtils.IS_OS_UNIX) && new File(filename).exists();
    }

    private String getFileFromResourcePath(final String filename) {
        return new File(resourceOnClasspath(filename).getFile()).getAbsolutePath();
    }

    private String getFileFromFileSystem(final String filename) {
        File fileToUpload = new File(filename);
        return fileToUpload.getAbsolutePath();
    }


    public void to(final WebElement uploadFileField) {

        String filePath = uploadableFilePathTo(uploadFileField).forFile(filename);

        uploadFileField.sendKeys(osSpecificPathOf(filePath));
    }

    private FilePathLocator uploadableFilePathTo(WebElement uploadFileField) {
        return (isRemoteDriver()) ? new RemoteFilePathLocator(uploadFileField) : new LocalFilePathLocator();
    }


    public boolean isRemoteDriver() {
        return remoteDriver;
    }

    public FileToUpload useRemoteDriver(boolean remoteDriver) {
        this.remoteDriver = remoteDriver;
        return this;
    }


    private String osSpecificPathOf(final String fileToUpload) {
        if (isAFullWindowsPath(fileToUpload)) {
            return windowsNative(fileToUpload);
        } else {
            return fileToUpload;
        }
    }

    private String windowsNative(final String fileToUpload) {
        String bareFilename = (fileToUpload.charAt(0) == '/') ? fileToUpload.substring(1) : fileToUpload;
        return StringUtils.replace(bareFilename,"/","\\");
    }

    public FileToUpload fromLocalMachine() {
        ConfigureFileDetector.forDriver(driver);
        return this;
    }

    private class LocalFilePathLocator implements FilePathLocator {

        public LocalFilePathLocator() {}

        @Override
        public String forFile(String filename) {
            return osSpecificPathOf(filename);
        }
    }

    interface FilePathLocator {
        String forFile(String filename);
    }

    class RemoteFilePathLocator implements FilePathLocator {

        private final WebElement uploadFileField;

        public RemoteFilePathLocator(WebElement uploadFileField) {

            this.uploadFileField = uploadFileField;
        }

        @Override
        public String forFile(String filename) {
            LocalFileDetector detector = new LocalFileDetector();
            File localFile = detector.getLocalFile(osSpecificPathOf(filename));
            WebElement resolvedField = (uploadFileField instanceof WebElementFacade)
                    ? ((WebElementFacade) uploadFileField).getWrappedElement() : uploadFileField;

            if (resolvedField instanceof RemoteWebElement) {
                ((RemoteWebElement) resolvedField).setFileDetector(detector);
            }
            return localFile.getAbsolutePath();
        }
    }
}

