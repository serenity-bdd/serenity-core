package net.thucydides.core.webdriver.integration;

import com.google.common.base.Function;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.screenshots.ScreenshotProcessor;
import net.thucydides.core.screenshots.SingleThreadScreenshotProcessor;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.StaticTestSite;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class WhenTakingLargeScreenshots {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private File screenshotDirectory;

    private WebDriver driver;
    private StaticTestSite testSite;

    private EnvironmentVariables environmentVariables;

    @Before
    public void createScreenshotDir() throws IOException {

        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();

        testSite = new StaticTestSite();
    }

    @After
    public void closeBrowser() {
        try {
            driver.quit();
        } catch (Exception e) { // Ignore - we don't really care
        }
    }

    @Test
    public void should_take_screenshot_with_specified_dimensions() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "640");
        environmentVariables.setProperty("thucydides.browser.height", "400");

        driver = testSite.open("phantomjs");

        ScreenshotProcessor screenshotProcessor = new SingleThreadScreenshotProcessor(environmentVariables);
        Photographer photographer = new Photographer(driver, screenshotDirectory,screenshotProcessor);
        File screenshotFile = photographer.takeScreenshot().get();

		waitUntilFileIsWritten(screenshotFile);

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        screenshotProcessor.terminate();
        waitUntilFileIsWritten(screenshotFile);
        assertThat(image.getWitdh(), is(greaterThan(350))); // In Windows the actual dimensions may be are slightly less
    }

    @Test
    public void should_only_store_one_file_for_identical_screenshots() throws Exception {

        driver = testSite.open("phantomjs");

        ScreenshotProcessor screenshotProcessor = new SingleThreadScreenshotProcessor(environmentVariables);
        Photographer photographer = new Photographer(driver, screenshotDirectory,screenshotProcessor);
        File screenshot1File = photographer.takeScreenshot().get();
        File screenshot2File = photographer.takeScreenshot().get();

        screenshotProcessor.terminate();
        waitUntilFileIsWritten(screenshot1File);
        waitUntilFileIsWritten(screenshot2File);
        assertThat(screenshot1File.getName(), equalTo(screenshot2File.getName()));

    }


    @Test
    public void should_resize_screenshot_if_requested() throws Exception {

        environmentVariables.setProperty("thucydides.browser.width", "640");
        environmentVariables.setProperty("thucydides.browser.height", "480");

        environmentVariables.setProperty("thucydides.resized.image.width", "300");

        driver = testSite.open("phantomjs");

        ScreenshotProcessor screenshotProcessor = new SingleThreadScreenshotProcessor(environmentVariables);
        Photographer photographer = new Photographer(driver, screenshotDirectory, screenshotProcessor);
        File screenshotFile = photographer.takeScreenshot().get();

        waitUntilFileIsWritten(screenshotFile);
        screenshotProcessor.terminate();

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(300));
    }


    @Test
    public void should_take_screenshots_correctly() throws IOException {
        driver = testSite.open("http:www.google.com", "screenshots/google.html", "phantomjs");

        Photographer photographer = new Photographer(driver, screenshotDirectory);
        File screenshotFile = photographer.takeScreenshot().get();

		waitUntilFileIsWritten(screenshotFile);

        assertThat(screenshotFile.exists(), is(true));
    }

	private void waitUntilFileIsWritten(File screenshotFile) {
        Wait<File> wait = new FluentWait<File>(screenshotFile)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(250, TimeUnit.MILLISECONDS);

        wait.until(new Function<File, Boolean>() {
            public Boolean apply(File file) {
                return file.exists();
            }
        });
    }

    @Mock
    Logger logger;

    @Mock
    FirefoxDriver mockDriver;

    @Test
    public void should_not_explode_when_firefox_cannot_take_a_large_screenshot() {

        when(mockDriver.getScreenshotAs(OutputType.BYTES)).thenThrow(new WebDriverException());

        Photographer photographer = new Photographer(mockDriver, screenshotDirectory) {
            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
        photographer.takeScreenshot();
    }
}
