package net.thucydides.core.pages.components;

import net.serenitybdd.core.webdriver.ConfigureFileDetector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.nio.file.Files.exists;

/**
 * A class that helps upload a file to an HTML form in using a fluent API.
 */
public class FileToUpload {
    /**
     *
     * The filename the user asked to upload
     */
    private final String requestedFilename;
    /**
     * The full path we resolved the requested filename to
     */
    private String resolvedFilename;

    static private final String WINDOWS_PATH_PATTERN = "^[A-Z]:\\\\.*";

    private static Pattern fullWindowsPath = Pattern.compile(WINDOWS_PATH_PATTERN);
    private final WebDriver driver;
    private boolean remoteDriver = false;

    public FileToUpload(WebDriver driver, final String requestedFilename) {
        this.driver = driver;
        this.requestedFilename = requestedFilename;
        this.resolvedFilename = resolveAsBestWeCan(requestedFilename);
    }

    private String resolveAsBestWeCan(String requestedFilename) {

        URL resourceOnTheClassPath = Optional.ofNullable(resourceOnClasspath(requestedFilename))
                                              .orElse(resourceOnClasspath(stripLeadingSlashFrom(requestedFilename)));

        String resolvedPath = (resourceOnTheClassPath != null) ? resourceOnTheClassPath.getPath() : getFileFromFileSystem(requestedFilename);

        return windowsSafe(resolvedPath);
    }

    private String windowsSafe(String resolvedPath) {
        return (SystemUtils.IS_OS_WINDOWS && resolvedPath.startsWith("/")) ? resolvedPath.substring(1) : resolvedPath;
    }

    private String stripLeadingSlashFrom(String requestedFilename) {
        return requestedFilename.startsWith("/") ? requestedFilename.substring(1) : requestedFilename;
    }

    private URL resourceOnClasspath(final String filename) {
        ClassLoader cldr = Thread.currentThread().getContextClassLoader();
        return cldr.getResource(filename);
    }

    public static boolean isAFullWindowsPath(final String filename) {
        return fullWindowsPath.matcher(filename).find();
    }

    private String getFileFromFileSystem(final String filename) {
        File fileToUpload = new File(filename);
        return fileToUpload.getAbsolutePath();
    }


    public void to(final WebElement uploadFileField) {

        String filePath = windowsSafe(uploadableFilePathTo(uploadFileField).forFile(resolvedFilename));

        checkThatFileExistsFor(filePath);

        uploadFileField.sendKeys(osSpecificPathOf(filePath));
    }

    private void checkThatFileExistsFor(String filePath) {
        if (!exists(Paths.get(filePath))) {
            throw new FileToUploadCouldNotBeFoundException(filePath);
        }
    }

    private FilePathLocator uploadableFilePathTo(WebElement uploadFileField) {
        return (isRemoteDriver()) ? new RemoteFilePathLocator(uploadFileField) : new LocalFilePathLocator();
    }


    private boolean isRemoteDriver() {
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

    public FileToUpload fromClasspath() {
        URL systemResource =  Optional.ofNullable(ClassLoader.getSystemResource(requestedFilename))
                .orElseThrow(() -> new InvalidArgumentException("File not found on classpath: " + requestedFilename));

        try {
            this.resolvedFilename = new File(systemResource.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            new InvalidArgumentException("File not found on classpath: " + requestedFilename);
        }
        return this;
    }

    private class LocalFilePathLocator implements FilePathLocator {

        LocalFilePathLocator() {}

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

        RemoteFilePathLocator(WebElement uploadFileField) {

            this.uploadFileField = uploadFileField;
        }

        @Override
        public String forFile(String filename) {
            LocalFileDetector detector = new LocalFileDetector();
            File localFile = detector.getLocalFile(osSpecificPathOf(filename));
            WebElement resolvedField = (uploadFileField instanceof WrapsElement)
                    ? ((WrapsElement) uploadFileField).getWrappedElement() : uploadFileField;

            if (resolvedField instanceof RemoteWebElement) {
                ((RemoteWebElement) resolvedField).setFileDetector(detector);
            }
            return localFile.getAbsolutePath();
        }
    }
}

