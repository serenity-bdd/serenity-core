package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.core.util.FileSystemUtils;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class WhenScreenshotsAreTaken {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private File screenshotDirectory;
    private File screenshotTaken;
    private File originalScreenshot;
    private File expectedResizedScreenshot;

    @Mock
    private FirefoxDriver driver;

    @Mock
    private HtmlUnitDriver htmlDriver;

    private Photographer photographer;


    class MockPhotographer extends Photographer {

        public MockPhotographer(final WebDriver driver, final File targetDirectory) {
            super(driver, targetDirectory);
        }

        public MockPhotographer(final WebDriver driver, final File targetDirectory, final BlurLevel blurLevel) {
            super(driver, targetDirectory, blurLevel);
        }

        @Override
        protected boolean driverCanTakeSnapshots() {
            return (driver != null);
        }

        @Override
        protected File blur(File srcFile) throws IOException {
            return srcFile;
        }
    }

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        prepareTemporaryFilesAndDirectories();
        photographer = new Photographer(driver,
                                        screenshotDirectory);
    }

    public void prepareTemporaryFilesAndDirectories() throws IOException {
        screenshotDirectory = temporaryDirectory.newFolder("screenshots");
        originalScreenshot = FileSystemUtils.getResourceAsFile("screenshots/google_page_1.png");
        expectedResizedScreenshot = FileSystemUtils.getResourceAsFile("screenshots/resized_google_page_1.png");
        screenshotTaken = temporaryDirectory.newFile("google_page_1.png");
        FileUtils.copyFile(originalScreenshot, screenshotTaken);
    }

    @Test
    public void the_driver_should_not_take_screenshots_if_the_driver_is_not_available() throws Exception {

        Photographer photographer = new MockPhotographer(null, screenshotDirectory);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        verify(driver,times(0)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_driver_should_capture_the_image() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void should_blur_screenshot_if_requested() throws Exception {

        Photographer outOfFocusPhotographer = new Photographer(driver, screenshotDirectory, BlurLevel.HEAVY);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        Optional<File> blurredScreenshot = outOfFocusPhotographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        assertThat(FileUtils.contentEquals(blurredScreenshot.get(), expectedResizedScreenshot), is(false));
    }

    @Test
    public void should_not_blur_screenshot_by_default() throws Exception {

        Photographer outOfFocusPhotographer = new Photographer(driver, screenshotDirectory);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        Optional<File> blurredScreenshot = outOfFocusPhotographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        assertThat(FileUtils.contentEquals(blurredScreenshot.get(), expectedResizedScreenshot), is(true));
    }

    @Test
    public void should_not_take_a_snapshot_if_unsupported_by_the_driver() throws Exception {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        Photographer photographer = new Photographer(htmlDriver, screenshotDirectory);
        photographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        verify(driver,never()).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void the_screenshot_should_be_stored_in_the_target_directory() throws IOException, InterruptedException{

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot().get().getName();
        waitUntilScreenshotsProcessed();
        File savedScreenshot = new File(screenshotDirectory, screenshotFile);
        savedScreenshot.setReadable(true);
        savedScreenshot.setWritable(true);
        assertThat(savedScreenshot.isFile(), is(true));
    }

    private void waitUntilScreenshotsProcessed() throws InterruptedException {
        photographer.getScreenshotProcessor().waitUntilDone();
        Thread.sleep(50);
    }

    @Test
    public void the_photographer_should_return_the_stored_screenshot_filename() throws IOException, InterruptedException {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        
        String savedFileName = photographer.takeScreenshot().get().getName();
        waitUntilScreenshotsProcessed();
        File savedScreenshot = new File(screenshotDirectory, savedFileName);
        
        assertThat(savedScreenshot.isFile(), is(true));
    }


//    @Test
//    public void the_photographer_should_provide_the_HTML_source_code_for_a_given_screenshot_if_configured() throws Exception {
//
//        environmentVariables.setProperty("thucydides.store.html.source","true");
//        Photographer photographer = new Photographer(driver, screenshotDirectory,
//                                                     Injectors.getInjector().getInstance(ScreenshotProcessor.class),
//                                                     null,
//                                                     environmentVariables);
//
//        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
//        when(driver.getPageSource()).thenReturn("<html/>");
//
//        File screenshotFile = photographer.takeScreenshot().get();
//        waitUntilScreenshotsProcessed();
//
//        File htmlSource = photographer.getMatchingSourceCodeFor(screenshotFile);
//
//        assertThat(htmlSource.isFile(), is(true));
//    }


    @Test
    public void calling_api_generates_a_filename_safe_hashed_name_for_the_screenshot() throws Exception {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot().get().getName();
        waitUntilScreenshotsProcessed();

        assertThat(screenshotFile, equalTo("6a0bceeab7f4fe24b6add7e76b1ff833_NONE.png"));
    }
    
    @Test
    public void by_default_screenshot_files_start_with_Screenshot() throws Exception {
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);

        String screenshotFile = photographer.takeScreenshot().get().getName();
        waitUntilScreenshotsProcessed();

        assertThat(screenshotFile, equalTo("6a0bceeab7f4fe24b6add7e76b1ff833_NONE.png"));
    }

    @Mock
    ScreenshotProcessor screenshotProcessor;

    @Test
    public void should_send_screenshots_to_screenshot_processor() {

        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.setScreenshotProcessor(screenshotProcessor);

        photographer.takeScreenshot();

        verify(screenshotProcessor).queueScreenshot((QueuedScreenshot) anyObject());
    }

    @Test
    public void should_blur_screenshots_if_blurScreenshots_option_is_present() throws Exception {
        Photographer photographer = new MockPhotographer(driver, screenshotDirectory, BlurLevel.HEAVY);
        photographer = spy(photographer);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        verify(photographer, times(1)).blur(any(File.class));
        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }

    @Test
    public void should_not_blur_screenshots_if_blurScreenshots_option_is_absent() throws Exception {
        Photographer photographer = new MockPhotographer(driver, screenshotDirectory, null);
        photographer = spy(photographer);
        when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(screenshotTaken);
        photographer.takeScreenshot();
        waitUntilScreenshotsProcessed();

        verify(photographer, times(0)).blur(any(File.class));
        verify(driver,times(1)).getScreenshotAs((OutputType<?>) anyObject());
    }
}
